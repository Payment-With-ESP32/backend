package com.example.payments.dto.esp32;

data class ESP32UpsertNodeRequestDto(
    val positionX: Double?,
    val positionY: Double?,
    val floor: Int?,
    val macAddress: String
)
