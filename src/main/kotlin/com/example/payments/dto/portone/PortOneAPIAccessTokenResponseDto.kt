package com.example.payments.dto.portone

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.ToString

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
data class PortOneAPIAccessTokenResponseDto(
    @JsonProperty("code")
    val code: Int = 0,

    @JsonProperty("message")
    val message: String?,

    @JsonProperty("response")
    val response: PortOneAPIAccessTokenResponseField
)

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
data class PortOneAPIAccessTokenResponseField(
    @JsonProperty("access_token")
    val accessToken: String,
)