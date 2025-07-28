package com.example.payments.dto.portone

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.ToString

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentCompleteRequestDto(
    val paymentId: String = ""
)
