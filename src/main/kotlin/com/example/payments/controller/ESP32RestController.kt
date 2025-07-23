package com.example.payments.controller;

import com.example.payments.dto.esp32.ESP32ChangeNodeRequestDto
import com.example.payments.dto.esp32.ESP32GetAllResponseDto
import com.example.payments.dto.esp32.ESP32UpsertMasterNodeRequestDto
import com.example.payments.dto.esp32.ESP32UpsertNodeRequestDto
import com.example.payments.service.ESP32MappingService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/esp32")
@CrossOrigin(origins = ["null", "none", "http://localhost:5173"])
class ESP32RestController(
    private val esp32MappingService: ESP32MappingService
) {
    @GetMapping
    fun getAllSubNodes(): ESP32GetAllResponseDto {
        return esp32MappingService.getNodes()
    }

    @PostMapping
    fun addSubNode(@RequestBody upsertDto: ESP32UpsertNodeRequestDto) {
        esp32MappingService.addNode(upsertDto)
    }

    @DeleteMapping("/{mac}")
    fun deleteSubNode(@PathVariable mac: String) {
        esp32MappingService.delNode(mac)
    }

    @PutMapping("/master")
    fun changeMasterNode(@RequestBody changeDto: ESP32UpsertMasterNodeRequestDto) {
        esp32MappingService.changeMaster(changeDto.macAddress)
    }

    @PatchMapping
    fun updateSubNode(@RequestBody updateDto: ESP32UpsertNodeRequestDto) {
        esp32MappingService.changePosition(updateDto)
    }

    @PutMapping("/{fromMac}")
    fun changeSubNode(
        @PathVariable fromMac: String,
        @RequestBody changeDto: ESP32ChangeNodeRequestDto
    ) {
        esp32MappingService.changeNode(fromMac, changeDto.toMac)
    }
}
