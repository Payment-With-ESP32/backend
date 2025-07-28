package com.example.payments.repository

import com.example.payments.entity.PaymentHistory
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface PaymentHistoryRepository : JpaRepository<PaymentHistory, Long> {
    fun findByPaymentId(paymentId: String): PaymentHistory?

    @Modifying
    @Transactional
    @Query("UPDATE PaymentHistory p set p.succeedAt = :now WHERE p.paymentId = :paymentId")
    fun updatePurchaseTimeByPaymentId(
        @Param("paymentId") paymentId: String,
        @Param("now") now: Instant
    ): Int
}