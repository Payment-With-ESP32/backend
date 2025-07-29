package com.example.payments.controller;

import com.example.payments.dto.esp32.*
import com.example.payments.service.ESP32MappingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/esp32")
@CrossOrigin(origins = ["null", "none", "http://localhost:5173"])
class ESP32RestController(
    private val esp32MappingService: ESP32MappingService
) {
    private final val uploadDir: String = "D:\\payment-system\\backend\\uploads\\"
    private final val fileRegex = Regex("(?i).*\\.(?:jpg|jpeg|png|webp)$")

    @GetMapping("/slaves")
    fun getAllSubNodes(): ESP32GetAllResponseDto {
        return esp32MappingService.getNodes()
    }

    @GetMapping("/master")
    fun getMasterNodeInfo(): ESP32GetMasterResponseDto {
        return esp32MappingService.getMaster()
    }

    @PutMapping("/transfer")
    fun changePersonPosition(@RequestBody transferDto: ESP32TransferTimeRequestDto) {
        esp32MappingService.transferTime(transferDto)
        return
    }

    @PostMapping("/master")
    @PutMapping("/master")
    fun changeMasterNode(@RequestBody changeDto: ESP32UpsertMasterRequestDto) {
        return esp32MappingService.upsertMaster(changeDto.toMac)
    }

    @PostMapping
    fun addSubNode(@RequestBody upsertDto: ESP32UpsertNodeRequestDto) {
        esp32MappingService.addNode(upsertDto)
        return
    }

    @DeleteMapping("/{mac}")
    fun deleteSubNode(@PathVariable mac: String) {
        esp32MappingService.delNode(mac)
        return
    }

    @PutMapping("/master")
    fun changeMasterNode(@RequestBody changeDto: ESP32UpsertMasterNodeRequestDto) {
        esp32MappingService.upsertMaster(changeDto.macAddress)
        return
    }

    @PatchMapping
    fun updateSubNode(@RequestBody updateDto: ESP32UpsertNodeRequestDto) {
        esp32MappingService.changePosition(updateDto)
        return
    }

    @PutMapping("/{fromMac}")
    fun changeSubNode(
        @PathVariable fromMac: String,
        @RequestBody changeDto: ESP32ChangeNodeRequestDto
    ) {
        esp32MappingService.changeNode(fromMac, changeDto.toMac)
        return
    }

    @PostMapping("/{mac}")
    fun preLongOrDenyNode(
        @PathVariable mac: String,
        @RequestBody preLongDto: ESP32PreLongRequestDto
    ) {
        esp32MappingService.preLongOrDenyNode(mac, preLongDto.time)
        return
    }

    @GetMapping("/images")
    fun getImageList(): List<String> {
        val dir = File(uploadDir)
        if (!dir.exists()) return emptyList()

        val fileList = dir.list()?.filter { it.matches(fileRegex) }
        if (fileList == null) return emptyList()
        return fileList
    }

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") file: MultipartFile) {
        val dir = File(uploadDir)
        if (!dir.exists()) dir.mkdirs()

        val targetFile = File(uploadDir + file.originalFilename.toString())
        file.transferTo(targetFile)
        return
    }

    @DeleteMapping("/image/{filename}")
    fun deleteImage(@PathVariable filename: String): ResponseEntity<Any> {
        val file = File(uploadDir + URLDecoder.decode(filename, StandardCharsets.UTF_8))
        if (file.exists()) {
            file.delete()
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }
}
