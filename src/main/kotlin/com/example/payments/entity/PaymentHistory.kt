package com.example.payments.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    indexes = [
        Index(name = "idx_imp_merchant_uid_set", columnList = "impUid, merchantUid"),
        Index(name = "idx_merchant_uid", columnList = "merchantUid"),
    ]
)
class PaymentHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val createdAt: Instant? = Instant.now(),
    var impUid: String = "",        // 결제 번호
    val merchantUid: String = "",   // 주문 번호
    val price: Long = 0,
    val position: Int = -1
)
