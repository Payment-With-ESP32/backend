package com.example.payments.config

import org.springframework.stereotype.Component
import java.util.concurrent.LinkedBlockingQueue

@Component
class SerialMessageQueue {
    private val queue = LinkedBlockingQueue<String>()

    fun push(message: String) {
        queue.offer(message)
    }

    @Throws(InterruptedException::class)
    fun take(): String = queue.take()

    fun isEmpty(): Boolean = queue.isEmpty()
}