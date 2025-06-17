package com.hitomatito.gamepulse_hud.core

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.StatFs
import java.io.File

class SystemInfoManager(private val context: Context) {
    
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    
    fun getMemoryUsage(): MemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val totalMemory = memoryInfo.totalMem / (1024 * 1024) // Convert to MB
        val availableMemory = memoryInfo.availMem / (1024 * 1024) // Convert to MB
        val usedMemory = totalMemory - availableMemory
        val usagePercent = (usedMemory.toFloat() / totalMemory.toFloat() * 100).toInt()
        
        return MemoryInfo(
            totalMB = totalMemory.toInt(),
            usedMB = usedMemory.toInt(),
            availableMB = availableMemory.toInt(),
            usagePercent = usagePercent
        )
    }
    
    fun getAppMemoryUsage(): AppMemoryInfo {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        
        val totalPss = memoryInfo.totalPss / 1024 // Convert to MB
        val dalvikPss = memoryInfo.dalvikPss / 1024
        val nativePss = memoryInfo.nativePss / 1024
        val otherPss = memoryInfo.otherPss / 1024
        
        return AppMemoryInfo(
            totalMB = totalPss,
            dalvikMB = dalvikPss,
            nativeMB = nativePss,
            otherMB = otherPss
        )
    }
    
    fun getStorageInfo(): StorageInfo {
        val internalDir = File(context.filesDir.absolutePath)
        val stat = StatFs(internalDir.path)
        
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        
        val totalGB = (totalBlocks * blockSize) / (1024 * 1024 * 1024)
        val availableGB = (availableBlocks * blockSize) / (1024 * 1024 * 1024)
        val usedGB = totalGB - availableGB
        val usagePercent = if (totalGB > 0) (usedGB.toFloat() / totalGB.toFloat() * 100).toInt() else 0
        
        return StorageInfo(
            totalGB = totalGB.toInt(),
            usedGB = usedGB.toInt(),
            availableGB = availableGB.toInt(),
            usagePercent = usagePercent
        )
    }
    
    fun getBatteryLevel(): Int {
        // This would require battery manager implementation
        // For now, return a placeholder value
        return -1 // -1 indicates not available
    }
    
    data class MemoryInfo(
        val totalMB: Int,
        val usedMB: Int,
        val availableMB: Int,
        val usagePercent: Int
    )
    
    data class AppMemoryInfo(
        val totalMB: Int,
        val dalvikMB: Int,
        val nativeMB: Int,
        val otherMB: Int
    )
    
    data class StorageInfo(
        val totalGB: Int,
        val usedGB: Int,
        val availableGB: Int,
        val usagePercent: Int
    )
}
