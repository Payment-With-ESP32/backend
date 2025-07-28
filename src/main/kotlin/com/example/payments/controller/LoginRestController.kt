package com.example.payments.controller

import com.example.payments.dto.login.ChangePasswordRequestDto
import com.example.payments.dto.login.LoginRequestDto
import com.example.payments.service.LoginService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = ["null", "none", "http://localhost:5173"])
class LoginRestController(
    private val loginService: LoginService
) {
    @PostMapping("/login")
    fun login(@RequestBody dto: LoginRequestDto): ResponseEntity<Any>
        = if (loginService.login(dto)) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()

    @PatchMapping
    fun changePassword(@RequestBody dto: ChangePasswordRequestDto): ResponseEntity<Any>
            = if (loginService.changePassword(dto)) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()

}