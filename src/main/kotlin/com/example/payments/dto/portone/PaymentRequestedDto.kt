package com.example.payments.dto.portone

import lombok.ToString

@ToString
data class PaymentRequestedDto(
    val paymentId: String = "",
    val amount: Long = 0,
    val macAddr: String
) {
    init {
        val macRegex = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
        require(macRegex.matches(macAddr.trim())) { "mac 정규식에 맞지 않습니다." }
    }
}
