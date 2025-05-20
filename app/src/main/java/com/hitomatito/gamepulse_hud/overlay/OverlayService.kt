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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.hitomatito.gamepulse_hud.R
import com.hitomatito.gamepulse_hud.core.SystemMetrics
import com.hitomatito.gamepulse_hud.core.TemperatureReader

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private val temperatureReader by lazy { TemperatureReader(this) }
    private val systemMetrics by lazy { SystemMetrics() }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        setupOverlay()
        startMetricsCollection()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    @SuppressLint("InflateParams")
    private fun setupOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        windowManager.addView(overlayView, params)
    }

    private fun startMetricsCollection() {
        systemMetrics.startFpsMonitoring { fps ->
            overlayView.post {
                updateOverlay(
                    fps = fps,
                    cpuTemp = temperatureReader.getTemperature("CPU"),
                    gpuTemp = temperatureReader.getTemperature("GPU")
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateOverlay(fps: Int, cpuTemp: Float?, gpuTemp: Float?) {
        overlayView.findViewById<TextView>(R.id.txtFPS).text = "FPS: $fps"
        overlayView.findViewById<TextView>(R.id.txtCPU).text =
            "CPU: ${cpuTemp?.let { "%.1f°C".format(it) } ?: "N/A"}"
        overlayView.findViewById<TextView>(R.id.txtGPU).text =
            "GPU: ${gpuTemp?.let { "%.1f°C".format(it) } ?: "N/A"}"
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
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Changed R.drawable.ic_stat_overlay to a system icon
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