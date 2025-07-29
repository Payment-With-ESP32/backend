package com.example.payments.config

import com.example.payments.`object`.ESP32Position
import com.example.payments.`object`.ESP32PositionDetail
import com.example.payments.service.JSerialCommManager
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.hibernate.jdbc.Expectation.None
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet
import kotlin.concurrent.thread

@Component
class ESP32ConnectionPoolCache(
    private val serialCommManager: JSerialCommManager,
    private val messageQueue: SerialMessageQueue
) {
    private val objectMapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }
    private lateinit var masterMac: String
    private var slaveMacs: MutableSet<ESP32Position> = HashSet()
    private val timeMap = ConcurrentHashMap<String, LinkedBlockingQueue<Long>>()

    @PostConstruct
    fun init() {
        val file = File("./ESP32Position.json")
        if (file.exists()) {
            val jsonText = file.readText(charset = StandardCharsets.UTF_8)
            val data: ESP32Data = objectMapper.readValue(jsonText, ESP32Data::class.java)
            masterMac = data.masterMac.trim().lowercase()
            data.slaveMacs.forEach { it.macAddress = it.macAddress.trim().lowercase() }
            slaveMacs = data.slaveMacs.toMutableSet()
        }

        thread(start = true) {
            while (true) {
                try {
                    // 메시지는 항상 "$TYPE/$VALUE"
                    val commands = messageQueue.take().split("/")
                    if (commands.size == 2) {
                        when (commands[0]) {
                            "MAC" -> {
                                if (masterMac != commands[1]) masterMac = commands[1].trim().lowercase()
                            }
                            "TIME" -> {
                                val timeParts = commands[1].split(",")
                                if (timeParts.size == 2) {
                                    val mac = timeParts[0].trim().lowercase()
                                    val time = timeParts[1].toLong()
                                    timeMap.computeIfAbsent(mac) { LinkedBlockingQueue() }.put(time)
                                }
                            }
                            else -> {}
                        }
                    }
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }
    }

    @Scheduled(cron = "0 0 */6 * * *")
    @PreDestroy
    fun saveDetails() {
        val slaveList = slaveMacs.sortedBy { it.macAddress }.toList()
        val saveData = ESP32Data(masterMac, slaveList)

        val file = File("./ESP32Position.json")
        objectMapper.writeValue(file, saveData)
    }

    fun getSlaves(): List<ESP32Position> = slaveMacs.toList()
    fun getMaster(): String = masterMac
    fun upsertSlave(macAddress: String, position: ESP32PositionDetail) {
        val loweredMacAddress = macAddress.trim().lowercase()

        val module = slaveMacs.find { it.macAddress == loweredMacAddress }
        if (module != null) {
            module.position = position
        } else {
            slaveMacs.add(ESP32Position(loweredMacAddress, position))
            val addCommand = "[ADD]$loweredMacAddress"
            serialCommManager.send(addCommand)
        }
    }
    fun changeModule(prevMacAddress: String, upcomingMacAddress: String) {
        val loweredPrevMacAddress = prevMacAddress.trim().lowercase()
        val loweredUpcomingMacAddress = upcomingMacAddress.trim().lowercase()

        val prevModule = slaveMacs.find { it.macAddress == loweredPrevMacAddress }
        if (prevModule != null) {
            deleteSlave(prevModule.macAddress)
            upsertSlave(loweredUpcomingMacAddress, prevModule.position)
            val swapCommand = "[SWAP]$loweredPrevMacAddress/$loweredUpcomingMacAddress"
            serialCommManager.send(swapCommand)
        }
    }
    fun upsertMaster(macAddress: String) {
        masterMac = macAddress.trim().lowercase()

        val connectCommand = "[INIT]${slaveMacs.joinToString(",") { it.macAddress }}"
        serialCommManager.send(connectCommand)
    }
    fun deleteSlave(macAddress: String) {
        val loweredMacAddress = macAddress.trim().lowercase()

        val dummy = ESP32Position(loweredMacAddress, ESP32PositionDetail())
        slaveMacs.remove(dummy)
        val delCommand = "[DEL]$macAddress"
        serialCommManager.send(delCommand)
    }
    fun sendPreLongServiceTime(macAddress: String, time: Long) {
        val loweredMacAddress = macAddress.trim().lowercase()
        val preLongCommand = "[PREL]$loweredMacAddress/${ if (time >= 0) time else -1 }"
        serialCommManager.send(preLongCommand)
    }

    fun transferTime(from: String, to: String, time: Long) {
        sendPreLongServiceTime(from, -1)
        sendPreLongServiceTime(to, time)
    }

    fun getLeftTime(macAddress: String): Long?  {
        val loweredMacAddress = macAddress.trim().lowercase()

        val getTimeCommand = "[TIME]$loweredMacAddress"
        serialCommManager.send(getTimeCommand)

        val queue = timeMap.computeIfAbsent(macAddress.lowercase()) { LinkedBlockingQueue() }
        return queue.poll(3, TimeUnit.MINUTES)
    }
}

data class ESP32Data(
    val masterMac: String,
    val slaveMacs: List<ESP32Position>
)