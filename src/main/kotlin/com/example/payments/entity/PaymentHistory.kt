package com.example.payments.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    indexes = [
        Index(name = "idx_payment_uid", columnList = "paymentId"),
    ]
)
class PaymentHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val createdAt: Instant? = Instant.now(),
    val succeedAt: Instant? = null,
    var paymentId: String = "",        // 결제 번호
    val price: Long = 0,
    val macAddress: String = ""
)
