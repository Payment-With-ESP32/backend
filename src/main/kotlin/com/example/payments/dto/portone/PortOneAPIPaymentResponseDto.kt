package com.example.payments.dto.portone

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.ToString

@ToString
data class PortOneAPIPaymentResponseDto(
    val code: Int,
    val message: String?,
    val response: PortOneAPIPaymentField
)

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
data class PortOneAPIPaymentField(
    val amount: Int?,

    @JsonProperty("apply_num")
    val applyNum: String?,

    @JsonProperty("imp_uid")
    val impUid: String?,

    @JsonProperty("merchant_uid")
    val merchantUid: String?,

    val name: String?,
)