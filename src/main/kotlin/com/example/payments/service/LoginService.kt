package com.example.payments.service

import com.example.payments.dto.login.ChangePasswordRequestDto
import com.example.payments.dto.login.LoginRequestDto
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Service
class LoginService {
    @PostConstruct
    fun init() {
        val dir = File("./password")
        if (!dir.exists()) dir.mkdirs()
    }


    fun login(dto: LoginRequestDto): Boolean {
        val passwordHashed = toHash(dto.password)
        return isFileExist(passwordHashed)
    }

    fun  changePassword(dto: ChangePasswordRequestDto): Boolean {
        val fromHashed = toHash(dto.from)
        val toHashed = toHash(dto.to)
        if (isFileExist(fromHashed)) {
            deleteFile(fromHashed)
            createFile(toHashed)
            return true
        }
        return false
    }

    private fun toHash(str: String): String {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(str.toByteArray(StandardCharsets.UTF_8))
            return byteToHex(digest)
        } catch(e: Exception) {
            throw Exception(e)
        }
    }

    private fun byteToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (byte in bytes) sb.append(String.format("%x", byte))
        return sb.toString()
    }

    private fun isFileExist(hashed: String) = File("./password/$hashed").exists()

    private fun createFile(hashed: String) {
        File("./password/$hashed").createNewFile()
    }

    private fun deleteFile(hashed: String) {
        File("./password/$hashed").delete()
    }
}