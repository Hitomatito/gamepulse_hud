package com.hitomatito.gamepulse_hud.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hitomatito.gamepulse_hud.overlay.OverlayService
import com.hitomatito.gamepulse_hud.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnSettings: Button
    private lateinit var btnToggleHUD: Button
    private lateinit var btnExit: Button
    private lateinit var tvStatusMessage: TextView
    
    private var isHUDRunning = false

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this)) {
                checkNotificationPermission()
            } else {
                updateStatusMessage("Permiso de superposición denegado")
                Toast.makeText(this, "Es necesario otorgar el permiso de superposición para usar el HUD.", Toast.LENGTH_LONG).show()
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startOverlayService()
            } else {
                updateStatusMessage("Permiso de notificación requerido")
                Toast.makeText(this, "Permiso de notificación requerido para el servicio en primer plano.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupButtonListeners()
        updateHUDStatus()
    }

    private fun initializeViews() {
        btnSettings = findViewById(R.id.btnSettings)
        btnToggleHUD = findViewById(R.id.btnToggleHUD)
        btnExit = findViewById(R.id.btnExit)
        tvStatusMessage = findViewById(R.id.tvStatusMessage)
    }

    private fun setupButtonListeners() {
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnToggleHUD.setOnClickListener {
            if (isHUDRunning) {
                stopHUD()
            } else {
                startHUDWithPermissions()
            }
        }

        btnExit.setOnClickListener {
            if (isHUDRunning) {
                stopHUD()
            }
            finish()
        }
    }

    private fun startHUDWithPermissions() {
        if (Settings.canDrawOverlays(this)) {
            checkNotificationPermission()
        } else {
            checkOverlayPermission()
        }
    }

    @SuppressLint("UseKtx")
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        } else {
            checkNotificationPermission()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startOverlayService()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(this, "Se necesita permiso de notificación para mostrar el estado del servicio.", Toast.LENGTH_LONG).show()
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startOverlayService()
        }
    }

    private fun startOverlayService() {
        if (!Settings.canDrawOverlays(this)) {
            updateStatusMessage("Permiso de superposición requerido")
            Toast.makeText(this, "El permiso de superposición es requerido.", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            startService(Intent(this, OverlayService::class.java))
            isHUDRunning = true
            updateHUDStatus()
            updateStatusMessage("HUD iniciado correctamente")
            Toast.makeText(this, "HUD iniciado", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            isHUDRunning = false
            updateHUDStatus()
            updateStatusMessage("Error al iniciar HUD")
            Toast.makeText(this, "Error al iniciar el HUD: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopHUD() {
        stopService(Intent(this, OverlayService::class.java))
        isHUDRunning = false
        updateHUDStatus()
        updateStatusMessage("HUD detenido")
        Toast.makeText(this, "HUD detenido", Toast.LENGTH_SHORT).show()
    }

    private fun updateHUDStatus() {
        if (isHUDRunning) {
            btnToggleHUD.text = "⏹️ Detener HUD"
            btnToggleHUD.setBackgroundColor(ContextCompat.getColor(this, R.color.button_secondary))
        } else {
            btnToggleHUD.text = "🎮 Iniciar HUD"
            btnToggleHUD.setBackgroundColor(ContextCompat.getColor(this, R.color.button_accent))
        }
    }

    private fun updateStatusMessage(message: String) {
        tvStatusMessage.text = message
    }

    override fun onResume() {
        super.onResume()
        // Check if overlay service is running when returning to the activity
        if (Settings.canDrawOverlays(this)) {
            updateStatusMessage("Listo para usar")
        } else {
            updateStatusMessage("Permiso de superposición requerido")
        }
    }
}