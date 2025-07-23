package com.example.payments.dto.portone

import lombok.ToString

@ToString
data class PaymentRequestedDto(
    val merchantUid: String = "",
    val amount: Long = 0,
    val macAddr: String
) {
    init {
        require(macAddr.length == 17) { "MAC 주소의 값은 17이어야 합니다. 2*6+5" }
    }
}
