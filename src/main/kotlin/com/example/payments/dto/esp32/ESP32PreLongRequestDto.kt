package com.example.payments.dto.esp32

data class ESP32PreLongRequestDto(
    val time: Long = 0
) {
    init {
        require(time >= -1) { "시간 값은 -1 이상의 정수형이어야 합니다." }
    }
}

