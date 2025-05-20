package com.hitomatito.gamepulse_hud.core

import android.content.Context
import android.util.Log
import java.io.File

class TemperatureReader(context: Context) {
    private val thermalFiles = mapOf(
        "CPU" to arrayOf(
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp"
        ),
        "GPU" to arrayOf(
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/devices/virtual/thermal/thermal_zone2/temp"
        )
    )

    fun getTemperature(type: String): Float? {
        thermalFiles[type]?.forEach { path ->
            try {
                File(path).takeIf { it.exists() }?.let {
                    return it.readText().trim().toFloatOrNull()?.div(1000)
                }
            } catch (e: Exception) {
                Log.e("TemperatureReader", "Error reading $type temp: ${e.message}")
            }
        }
        return null
    }
}