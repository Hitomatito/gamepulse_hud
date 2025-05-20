package com.hitomatito.gamepulse_hud.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hitomatito.gamepulse_hud.overlay.OverlayService
import com.hitomatito.gamepulse_hud.R

class MainActivity : AppCompatActivity() {

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this)) {
                checkNotificationPermission()
            } else {
                Toast.makeText(this, "Permiso de superposición requerido.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startOverlayServiceAndFinish()
            } else {
                Toast.makeText(this, "Permiso de notificación requerido para el servicio en primer plano.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Puede ser útil para mostrar mensajes o un botón de reintento

        checkOverlayPermission()
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
                    startOverlayServiceAndFinish()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Opcional: Mostrar una UI explicando por qué se necesita el permiso
                    Toast.makeText(this, "Se necesita permiso de notificación para mostrar el estado del servicio.", Toast.LENGTH_LONG).show()
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startOverlayServiceAndFinish()
        }
    }

    private fun startOverlayServiceAndFinish() {
        startService(Intent(this, OverlayService::class.java))
        finish()
    }
}