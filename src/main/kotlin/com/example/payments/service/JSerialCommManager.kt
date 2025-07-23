package com.example.payments.service

import com.example.payments.config.SerialMessageQueue
import com.example.payments.properties.ESP32UartProperty
import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.*


@Component
class JSerialCommManager(
    private val esp32UartProperty: ESP32UartProperty,
    private val messageQueue: SerialMessageQueue
) {
    private val buffer: StringBuilder = StringBuilder()
    private val notSentBuffer: MutableList<String> = LinkedList()

    @Volatile
    private var serialPort: SerialPort? = null

    @Volatile
    private var outputStream: OutputStream? = null

    @Volatile
    private var running = true

    private val healthCheckValue: String = "[HEALTH]"

    lateinit var macAddress: String

    @Scheduled(fixedDelay = 1000)
    fun checkSerialHealth() {
        try {
            send(healthCheckValue)
        } catch (ex: IOException) {
            reconnect()
            processNotSentMessage()
        } catch (_: Exception) {}
    }

    @PostConstruct
    fun connect() {
        closeSerial()
        val port = SerialPort.getCommPort(esp32UartProperty.port).apply {
            this.baudRate = esp32UartProperty.baudRate
            openPort(1000)
        }
        if (port.isOpen) {
            serialPort = port
            outputStream = port.outputStream
            setupListener(port)
        } else {
            serialPort = null
        }
    }

    fun setupListener(port: SerialPort) {
        port.removeDataListener()
        port.addDataListener(object : SerialPortDataListener {
            override fun getListeningEvents(): Int = SerialPort.LISTENING_EVENT_DATA_AVAILABLE
            override fun serialEvent(event: SerialPortEvent) {
                if (event.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return

                try {
                    val available = port.bytesAvailable()
                    if (available <= 0) return

                    val data = ByteArray(available)
                    port.readBytes(data, data.size)
                    val incoming = String(data, StandardCharsets.UTF_8)

                    handleIncomingMessage(incoming)
                } catch (ex: IOException) {
                    println("⚠\uFE0F Read error: ${ex.message}, reconnecting...")
                    reconnect()
                } catch (ex: Exception) {
                    println("⚠️ Unexpected error: ${ex.message}")
                }
            }
        })
    }

    @PreDestroy
    fun stop() {
        running = false
        closeSerial()
    }

    fun reconnect() {
        closeSerial()
        Thread.sleep(1000)
        connect()
    }

    private fun closeSerial() {
        try {
            serialPort?.closePort()
        } catch (_: Exception) {}
        serialPort = null
    }

    fun send(message: String) {
        val sendMessage = "${message.trim()}//${LocalDateTime.now()}\n"
        if (!message.startsWith(healthCheckValue)) println("📤 Sent: $message")
        processNotSentMessage()

        try {
            sendToUart(sendMessage)
        } catch (ex: IOException) {
            println("⚠\uFE0F write error: ${ex.message}, reconnecting...")
            try {
                reconnect()
                processNotSentMessage()
                sendToUart(sendMessage)
            } catch (ex: Exception) {
                notSentBuffer.add(sendMessage)
            }
        } catch (ex: Exception) {
            println("⚠️ Error while sending message: ${ex.message}")
        }
    }

    private fun sendToUart(message: String) {
        outputStream?.write(message.toByteArray(StandardCharsets.UTF_8))
        outputStream?.flush()
    }

    private fun processNotSentMessage() {
        val iterator = notSentBuffer.iterator()
        while (iterator.hasNext()) {
            val msg = iterator.next()
            try {
                sendToUart(msg)
                iterator.remove()
            } catch (_: Exception) {}
        }
    }

    private fun handleIncomingMessage(message: String) {
        buffer.append(message)
        while (buffer.contains("\n")) {
            val lineEnd = buffer.indexOf("\n")
            val fullLine = buffer.substring(0, lineEnd).trimEnd()
            buffer.delete(0, lineEnd + 1)

            if (fullLine.startsWith("[ESP]")) {
                val payload = fullLine.substring("[ESP]".length)
                if (payload.startsWith("MAC/")) messageQueue.push(payload.trim())

                println(fullLine)
            }
        }
    }
}