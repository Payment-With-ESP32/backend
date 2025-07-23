package com.example.payments.controller

import com.example.payments.dto.portone.PaymentCompleteRequestDto
import com.example.payments.dto.portone.PaymentRequestedDto
import com.example.payments.dto.portone.PortOneAPIPaymentResponseDto
import com.example.payments.service.JSerialCommManager
import com.example.payments.service.PaymentService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = ["null", "none", "http://localhost:5173"])
class PaymentRestController(
    private val paymentService: PaymentService,
    private val jSerialCommManager: JSerialCommManager
) {

    @PostMapping("/complete")
    fun paymentComplete(@RequestBody paymentCompleteRequestDto: PaymentCompleteRequestDto): String {
        val apiResponse: PortOneAPIPaymentResponseDto? = paymentService.getPortonePaymentResponse(paymentCompleteRequestDto)
        return apiResponse.toString()
    }

    @PostMapping("/prepare")
    fun paymentRequested(@RequestBody paymentRequestedDto: PaymentRequestedDto): String {
        paymentService.setPaymentRequest(paymentRequestedDto)
        return paymentRequestedDto.toString()
    }
}