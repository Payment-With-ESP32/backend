package com.example.payments.`object`

import com.example.payments.dto.esp32.ESP32UpsertNodeRequestDto

data class ESP32Position(
    var macAddress: String,
    var position: ESP32PositionDetail
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ESP32Position) return false

        return macAddress == other.macAddress
    }

    override fun hashCode(): Int = macAddress.trim().hashCode()
}

data class ESP32PositionDetail(
    var x: Double = -1.0,
    var y: Double = -1.0,
    var floor: Int = -1
) {
    constructor(dto: ESP32UpsertNodeRequestDto): this(
        x = dto.positionX ?: -1.0,
        y = dto.positionY ?: -1.0,
        floor = dto.floor ?: -1
    )
}