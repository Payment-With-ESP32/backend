package com.example.payments.properties

import lombok.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "data.portone")
@ToString
data class PortOneApiKeyProperty(
    var apiKey: String = "",
    var apiSecret: String = "",
    var apiV2Secret: String = ""
)