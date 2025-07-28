package com.example.payments.service

import com.example.payments.dto.portone.*
import com.example.payments.entity.PaymentHistory
import com.example.payments.repository.PaymentHistoryRepository
import com.example.payments.properties.PortOneApiKeyProperty
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.Instant

@Service
class PaymentService(
    private val portOneApiKeyProperty: PortOneApiKeyProperty,
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    val webClient: WebClient = WebClient.builder().build()

    @Transactional
    fun setPaymentRequest(dto: PaymentRequestedDto) {
        val paymentHistory = PaymentHistory(paymentId=dto.paymentId, price=dto.amount, macAddress=dto.macAddr)
        paymentHistoryRepository.save(paymentHistory)
    }

    @Transactional
    fun updatePurchaseSucceedTime(purchaseId: String): Int {
        val affectedLows = paymentHistoryRepository.updatePurchaseTimeByPaymentId(
            purchaseId,
            Instant.now()
        )
        return affectedLows
    }

    fun completePayment(dto: PaymentCompleteRequestDto): ResponseEntity<PaymentVerificationResponseDto> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Authorization"] = "PortOne: " + portOneApiKeyProperty.apiV2Secret

        val paymentResponse = webClient.get()
            .uri("https://api.portone.io/payments/" + dto.paymentId)
            .headers { it.set("Authorization", "PortOne " + portOneApiKeyProperty.apiV2Secret)}
            .retrieve()
            .bodyToMono(PaymentVerificationResponseDto::class.java)
            .block()

        updatePurchaseSucceedTime(dto.paymentId)
        val paymentHistory = paymentHistoryRepository.findByPaymentId(dto.paymentId)
        paymentResponse?.let { it.macAddress = paymentHistory?.macAddress.toString() }

        return ResponseEntity.ok(paymentResponse)
    }
}