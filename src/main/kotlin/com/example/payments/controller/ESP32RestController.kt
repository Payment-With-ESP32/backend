package com.example.payments.controller;

import com.example.payments.dto.esp32.*
import com.example.payments.service.ESP32MappingService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/esp32")
@CrossOrigin(origins = ["null", "none", "http://localhost:5173"])
class ESP32RestController(
    private val esp32MappingService: ESP32MappingService
) {
    @GetMapping("/slaves")
    fun getAllSubNodes(): ESP32GetAllResponseDto {
        return esp32MappingService.getNodes()
    }

    @GetMapping("/master")
    fun getMasterNodeInfo(): ESP32GetMasterResponseDto {
        return esp32MappingService.getMaster()
    }

    @PostMapping("/master")
    @PutMapping("/master")
    fun changeMasterNode(@RequestBody changeDto: ESP32UpsertMasterRequestDto) {
        return esp32MappingService.upsertMaster(changeDto.toMac)
    }

    @PostMapping
    fun addSubNode(@RequestBody upsertDto: ESP32UpsertNodeRequestDto) {
        esp32MappingService.addNode(upsertDto)
    }

    @DeleteMapping("/{mac}")
    fun deleteSubNode(@PathVariable mac: String) {
        val parsedMac = mac.replace("-", ":")
        esp32MappingService.delNode(parsedMac)
    }

    @PutMapping("/master")
    fun changeMasterNode(@RequestBody changeDto: ESP32UpsertMasterNodeRequestDto) {
        esp32MappingService.upsertMaster(changeDto.macAddress)
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
        val parsedMac = fromMac.replace("-", ":")
        esp32MappingService.changeNode(parsedMac, changeDto.toMac)
    }

    @PostMapping("/{mac}")
    fun preLongOrDenyNode(
        @PathVariable mac: String,
        @RequestBody preLongDto: ESP32PreLongRequestDto
    ) {
        val parsedMac = mac.replace("-", ":")
        esp32MappingService.preLongOrDenyNode(parsedMac, preLongDto.time)
    }
}
