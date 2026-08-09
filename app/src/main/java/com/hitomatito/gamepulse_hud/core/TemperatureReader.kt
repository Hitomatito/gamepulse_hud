package com.hitomatito.gamepulse_hud.core

import android.util.Log
import java.io.File

class TemperatureReader {
    private val thermalDir = "/sys/class/thermal"

    fun getTemperature(type: String): Float? {
        val zones = File(thermalDir).listFiles { f -> f.name.startsWith("thermal_zone") }
            ?: return null

        zones.forEach { zone ->
            val typeFile = File(zone, "type")
            val tempFile = File(zone, "temp")
            if (!typeFile.exists() || !tempFile.exists()) return@forEach

            val zoneType = try {
                typeFile.readText().trim().lowercase()
            } catch (e: Exception) {
                return@forEach
            }

            // Buscar por tipo de zona, no por índice (el índice varía por dispositivo/kernel)
            val matches = when (type) {
                "CPU" -> zoneType.startsWith("cpu")
                "GPU" -> zoneType.startsWith("gpu") // cubre "gpu-*" y "gpuss-*"
                else -> return null
            }
            if (!matches) return@forEach

            return try {
                tempFile.readText().trim().toFloatOrNull()?.div(1000)
            } catch (e: Exception) {
                Log.e("TemperatureReader", "Error reading $type temp: ${e.message}")
                null
            }
        }
        return null
    }
}