package com.example.payments.service

import com.example.payments.config.ESP32ConnectionPoolCache
import com.example.payments.dto.esp32.ESP32UpsertNodeRequestDto
import com.example.payments.dto.esp32.ESP32GetAllResponseDto
import com.example.payments.`object`.ESP32PositionDetail
import org.springframework.stereotype.Service

@Service
class ESP32MappingService(
    private val esp32ConnectionCache: ESP32ConnectionPoolCache
) {
    fun addNode(addRequest: ESP32UpsertNodeRequestDto) {
        val detail = ESP32PositionDetail(addRequest)
        esp32ConnectionCache.upsertSlave(addRequest.macAddress, detail)
    }

    fun delNode(macAddress: String) {
        esp32ConnectionCache.deleteSlave(macAddress)
    }

    fun getNodes(): ESP32GetAllResponseDto {
        val slaves = esp32ConnectionCache.getSlaves()
        return ESP32GetAllResponseDto(slaves)
    }

    fun changePosition(updateRequest: ESP32UpsertNodeRequestDto) {
        val detail = ESP32PositionDetail(updateRequest)
        esp32ConnectionCache.upsertSlave(updateRequest.macAddress, detail)
    }

    fun changeMaster(macAddress: String) {
        esp32ConnectionCache.changeMaster(macAddress)
    }

    fun changeNode(fromMac: String, toMac: String) {
        esp32ConnectionCache.changeModule(fromMac, toMac);
    }
}