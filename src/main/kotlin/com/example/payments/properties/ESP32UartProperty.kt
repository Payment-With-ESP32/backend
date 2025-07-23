package com.example.payments.properties

import lombok.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "data.esp32")
@ToString
data class ESP32UartProperty(
    var baudRate: Int = 0,
    var port: String = "",
)
