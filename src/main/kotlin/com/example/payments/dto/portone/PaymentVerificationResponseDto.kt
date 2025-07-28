package com.example.payments.dto.portone

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentVerificationResponseDto(
    val status: String,
    val id: String,
    val version: String,
    val requestedAt: String,
    val updatedAt: String,
    val statusChangedAt: String,
    val orderName: String,
    val amount: PaymentVerificationAmount,
    val currency: String,
    val paidAt: String,
    val receiptUrl: String,
    var macAddress: String? = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentVerificationAmount(
    val total: Int,
    val taxFree: Int,
    val vat: Int,
    val supply: Int,
    val discount: Int,
    val cancelled: Int,
    val cancelledTaxFree: Int,
)