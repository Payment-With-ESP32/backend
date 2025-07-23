package com.example.payments.dto.portone

import com.fasterxml.jackson.annotation.JsonProperty

data class PortOneAPIAccessTokenRequestDto(
    @JsonProperty("imp_key")
    val impKey: String,

    @JsonProperty("imp_secret")
    val impSecret: String
)