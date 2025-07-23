package com.example.payments.service

import com.example.payments.dto.portone.*
import com.example.payments.entity.PaymentHistory
import com.example.payments.repository.PaymentHistoryRepository
import com.example.payments.properties.PortOneApiKeyProperty
import jakarta.transaction.Transactional
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class PaymentService(
    private val portOneApiKeyProperty: PortOneApiKeyProperty,
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    val webClient: WebClient = WebClient.builder().build()

    @Transactional
    fun setPaymentRequest(dto: PaymentRequestedDto) {
        val paymentHistory = PaymentHistory(merchantUid=dto.merchantUid, price=dto.amount)
        paymentHistoryRepository.save(paymentHistory)
    }

    @Transactional
    fun updatePaymentImpUidByMerchantUid(merchantUid: String, impUid: String) {
        val affectedLows = paymentHistoryRepository.updateImpUidByMerchantUid(
            merchantUid,
            impUid
        )
//        println(affectedLows)
    }

    fun getPortonePaymentResponse(paymentCompleteRequestDto: PaymentCompleteRequestDto): PortOneAPIPaymentResponseDto? {
        val requestBody = PortOneAPIAccessTokenRequestDto(
            portOneApiKeyProperty.apiKey,
            portOneApiKeyProperty.apiSecret
        )
        val portoneAccessToken: String = webClient.post()
            .uri("https://api.iamport.kr/users/getToken")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(PortOneAPIAccessTokenResponseDto::class.java)
            .block()?.response?.accessToken.toString()

        val response: PortOneAPIPaymentResponseDto? = webClient.get()
            .uri("https://api.iamport.kr/payments/" + paymentCompleteRequestDto.impUid)
            .header("Authorization", portoneAccessToken)
            .retrieve()
            .bodyToMono(PortOneAPIPaymentResponseDto::class.java)
            .block()

        response?.response?.impUid?.let {
            updatePaymentImpUidByMerchantUid(
                paymentCompleteRequestDto.merchantUid,
                it
            )
        }

        return response
    }
}