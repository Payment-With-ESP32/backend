package com.example.payments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PaymentsApplication

fun main(args: Array<String>) {
	runApplication<PaymentsApplication>(*args)
}
