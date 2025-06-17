package com.hitomatito.gamepulse_hud.overlay

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.hitomatito.gamepulse_hud.R
import com.hitomatito.gamepulse_hud.core.SystemMetrics
import com.hitomatito.gamepulse_hud.core.TemperatureReader
import com.hitomatito.gamepulse_hud.utils.PreferencesManager

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: OverlayView
    private lateinit var preferencesManager: PreferencesManager
    private val temperatureReader by lazy { TemperatureReader(this) }
    private val systemMetrics by lazy { SystemMetrics() }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        
        // Verificar permisos antes de crear la overlay
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        
        try {
            setupOverlay()
            startMetricsCollection()
            startForeground(NOTIFICATION_ID, createNotification())
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    @SuppressLint("InflateParams")
    private fun setupOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = OverlayView(this)
        val windowType =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = preferencesManager.overlayPositionX
            y = preferencesManager.overlayPositionY
        }

        try {
            windowManager.addView(overlayView, params)
            // Pasar el WindowManager y los parámetros al OverlayView para el manejo de toques
            overlayView.setWindowManager(windowManager, params)
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
            throw e
        }
    }

    private fun startMetricsCollection() {
        systemMetrics.startFpsMonitoring { fps ->
            overlayView.post {
                overlayView.updateMetrics(
                    fps = fps,
                    cpuTemp = temperatureReader.getTemperature("CPU"),
                    gpuTemp = temperatureReader.getTemperature("GPU")
                )
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotification(): Notification {
        val channelId = "overlay_channel_01"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Overlay Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canal para el servicio de superposición GamePulse HUD"
                lockscreenVisibility = Notification.VISIBILITY_SECRET
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("GamePulse HUD en ejecución")
            .setContentText("Monitor de rendimiento activo")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        systemMetrics.stopMonitoring()
        if (::overlayView.isInitialized && ::windowManager.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}