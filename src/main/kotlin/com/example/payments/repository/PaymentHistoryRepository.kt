package com.example.payments.repository

import com.example.payments.entity.PaymentHistory
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PaymentHistoryRepository : JpaRepository<PaymentHistory, Long> {
    fun findByImpUid(impUid: String): PaymentHistory?
    fun findByMerchantUid(merchantUid: String): PaymentHistory?

    @Modifying
    @Transactional
    @Query("UPDATE PaymentHistory p set p.impUid = :impUid WHERE p.merchantUid = :merchantUid")
    fun updateImpUidByMerchantUid(
        @Param("merchantUid") merchantUid: String,
        @Param("impUid") impUid: String
    ): Int
}