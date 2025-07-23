package com.example.payments.config

import com.example.payments.`object`.ESP32Position
import com.example.payments.`object`.ESP32PositionDetail
import com.example.payments.service.JSerialCommManager
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets
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

    @PostConstruct
    fun init() {
        val file = File("./ESP32Position.json")
        if (file.exists()) {
            val jsonText = file.readText(charset = StandardCharsets.UTF_8)
            val data: ESP32Data = objectMapper.readValue(jsonText, ESP32Data::class.java)
            masterMac = data.masterMac.trim()
            data.slaveMacs.forEach { it.macAddress = it.macAddress.trim() }
            slaveMacs = data.slaveMacs.filter { it.macAddress != masterMac }.toMutableSet()
        }

        thread(start = true) {
            while (true) {
                try {
                    // 메시지는 항상 "$TYPE/$VALUE"
                    val commands = messageQueue.take().split("/")
                    if (commands.size == 2) {
                        when (commands[0]) {
                            "MAC" -> {
                                if (masterMac != commands[1]) masterMac = commands[1]
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
        val module = slaveMacs.find { it.macAddress == macAddress }
        if (module != null) {
            module.position = position
        } else {
            slaveMacs.add(ESP32Position(macAddress, position))
            val addCommand = "[ADD]$macAddress"
            serialCommManager.send(addCommand)
        }
    }
    fun changeModule(prevMacAddress: String, upcomingMacAddress: String) {
        val prevModule = slaveMacs.find { it.macAddress == prevMacAddress }
        if (prevModule != null) {
            deleteSlave(prevModule.macAddress)
            upsertSlave(upcomingMacAddress, prevModule.position)
            val swapCommand = "[SWAP]$prevMacAddress/$upcomingMacAddress"
            serialCommManager.send(swapCommand)
        }
    }
    fun upsertMaster(macAddress: String) {
        masterMac = macAddress
        val connectCommand = "[INIT]${slaveMacs.joinToString(",") { it.macAddress }}"
        serialCommManager.send(connectCommand)
    }
    fun deleteSlave(macAddress: String) {
        val dummy = ESP32Position(macAddress.trim(), ESP32PositionDetail())
        slaveMacs.remove(dummy)
        val delCommand = "[DEL]$macAddress"
        serialCommManager.send(delCommand)
    }
    fun sendPreLongServiceTime(macAddress: String, time: Int) {
        val preLongCommand = "[PREL]" + when (time) {
            -1 -> "OFF"
            0 -> "ON/0"
            else -> "ON/$time"
        }
        serialCommManager.send(preLongCommand)
        println(preLongCommand)
    }
}

data class ESP32Data(
    val masterMac: String,
    val slaveMacs: List<ESP32Position>
)