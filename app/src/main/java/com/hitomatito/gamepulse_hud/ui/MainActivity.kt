package com.hitomatito.gamepulse_hud.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.hitomatito.gamepulse_hud.overlay.OverlayService
import com.hitomatito.gamepulse_hud.utils.GamerToast
import com.hitomatito.gamepulse_hud.utils.PreferencesManager
import com.hitomatito.gamepulse_hud.R

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var btnSettings: MaterialButton
    private lateinit var btnToggleHUD: MaterialButton
    private lateinit var btnExit: MaterialButton
    private lateinit var tvStatusMessage: TextView
    private lateinit var tvAppTitle: TextView
    private lateinit var tvAppDescription: TextView
    private lateinit var imgStatusDot: View
    private lateinit var imgAppIcon: ImageView
    private lateinit var auraGlow: ImageView
    private lateinit var toggleGlow: View
    private lateinit var settingsGlow: View
    private lateinit var titleUnderline: View
    private lateinit var accentBar: View
    private lateinit var scanline: View
    private val eqBars: MutableList<View> = mutableListOf()
    private val ambientAnimations: MutableList<ObjectAnimator> = mutableListOf()
    private var toggleGlowAnimator: ObjectAnimator? = null
    private var didEnterAnimations = false
    
    private var isHUDRunning = false

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this)) {
                checkNotificationPermission()
            } else {
                updateStatusMessage("Permiso de superposición denegado")
                GamerToast.show(this, "Es necesario otorgar el permiso de superposición para usar el HUD.", longDuration = true)
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startOverlayService()
            } else {
                updateStatusMessage("Permiso de notificación requerido")
                GamerToast.show(this, "Permiso de notificación requerido para el servicio en primer plano.", longDuration = true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferencesManager = PreferencesManager(this)
        initializeViews()
        setupButtonListeners()
        applyNeonAccent()
        updateHUDStatus()
    }

    private fun initializeViews() {
        btnSettings = findViewById(R.id.btnSettings)
        btnToggleHUD = findViewById(R.id.btnToggleHUD)
        btnExit = findViewById(R.id.btnExit)
        tvStatusMessage = findViewById(R.id.tvStatusMessage)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        tvAppDescription = findViewById(R.id.tvAppDescription)
        imgStatusDot = findViewById(R.id.imgStatusDot)
        imgAppIcon = findViewById(R.id.imgAppIcon)
        auraGlow = findViewById(R.id.auraGlow)
        toggleGlow = findViewById(R.id.toggleGlow)
        settingsGlow = findViewById(R.id.settingsGlow)
        titleUnderline = findViewById(R.id.titleUnderline)
        accentBar = findViewById(R.id.accentBar)
        scanline = findViewById(R.id.scanline)
        eqBars.addAll(
            listOf(
                findViewById(R.id.eqBar1),
                findViewById(R.id.eqBar2),
                findViewById(R.id.eqBar3),
                findViewById(R.id.eqBar4),
                findViewById(R.id.eqBar5)
            )
        )
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
                    GamerToast.show(this, "Se necesita permiso de notificación para mostrar el estado del servicio.", longDuration = true)
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
GamerToast.show(this, "El permiso de superposición es requerido.")
                return
        }
        
        try {
            startService(Intent(this, OverlayService::class.java))
            isHUDRunning = true
            updateHUDStatus()
            updateStatusMessage("HUD iniciado correctamente")
            GamerToast.show(this, "HUD iniciado")
        } catch (e: Exception) {
            e.printStackTrace()
            isHUDRunning = false
            updateHUDStatus()
            updateStatusMessage("Error al iniciar HUD")
            GamerToast.show(this, "Error al iniciar el HUD: ${e.message}", longDuration = true)
        }
    }

    private fun stopHUD() {
        stopService(Intent(this, OverlayService::class.java))
        isHUDRunning = false
        updateHUDStatus()
        updateStatusMessage("HUD detenido")
        GamerToast.show(this, "HUD detenido")
    }

    private fun updateHUDStatus() {
        if (isHUDRunning) {
            btnToggleHUD.setText(R.string.toggle_hud_stop)
            btnToggleHUD.setNeonStyle(R.drawable.bg_btn_stop, R.drawable.ic_stop)
            toggleGlow.setBackgroundResource(R.drawable.bg_aura_red)
            toggleGlow.backgroundTintList = null
            toggleGlow.visibility = View.VISIBLE
            startToggleGlowPulse()
        } else {
            btnToggleHUD.setText(R.string.toggle_hud_start)
            btnToggleHUD.setNeonStyle(R.drawable.bg_btn_start, R.drawable.ic_play)
            toggleGlow.setBackgroundResource(R.drawable.bg_aura_green)
            // Solo teñir con el color personalizado si el usuario lo cambió
            // (por defecto es blanco y se conserva el verde de la marca)
            toggleGlow.backgroundTintList = if (preferencesManager.textColor != PreferencesManager.DEFAULT_TEXT_COLOR) {
                ColorStateList.valueOf(preferencesManager.textColor)
            } else {
                null
            }
            toggleGlow.visibility = View.VISIBLE
            stopToggleGlowPulse()
            toggleGlow.alpha = 1f
        }
    }

    private fun startToggleGlowPulse() {
        toggleGlowAnimator?.cancel()
        toggleGlowAnimator = ObjectAnimator.ofFloat(toggleGlow, View.ALPHA, 0.45f, 1f).apply {
            duration = 1100
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    private fun stopToggleGlowPulse() {
        toggleGlowAnimator?.cancel()
        toggleGlowAnimator = null
    }

    /**
     * Aplica el color personalizado de Ajustes a los acentos neón de la pantalla
     * principal (auras, subrayado, barras y ecualizador). Con el color por defecto
     * (blanco) se conserva el cian de la marca.
     */
    private fun applyNeonAccent() {
        val custom = preferencesManager.textColor
        val accent = if (custom != PreferencesManager.DEFAULT_TEXT_COLOR) custom
            else ContextCompat.getColor(this, R.color.game_cyan)
        val tintList = ColorStateList.valueOf(accent)

        // El filtro SRC_IN conserva el degradado de transparencia del aura
        auraGlow.setColorFilter(accent, PorterDuff.Mode.SRC_IN)
        settingsGlow.backgroundTintList = tintList
        titleUnderline.backgroundTintList = tintList
        accentBar.backgroundTintList = tintList
        eqBars.forEach { it.backgroundTintList = tintList }
        scanline.backgroundTintList = tintList
        imgAppIcon.imageTintList = tintList
        tvStatusMessage.setTextColor(accent)
    }

    private fun MaterialButton.setNeonStyle(bgRes: Int, iconRes: Int) {
        setBackgroundResource(bgRes)
        icon = ContextCompat.getDrawable(this@MainActivity, iconRes)
        iconTint = ColorStateList.valueOf(Color.WHITE)
    }

    private fun updateStatusMessage(message: String) {
        tvStatusMessage.text = message
    }

    private fun startAmbientAnimations() {
        stopAmbientAnimations()
        val reverse = ValueAnimator.INFINITE to ValueAnimator.REVERSE
        val invert = ValueAnimator.INFINITE to ValueAnimator.RESTART

        // Pulso del punto de estado
        ambientAnimations += ObjectAnimator.ofFloat(imgStatusDot, View.ALPHA, 0.35f, 1f).apply {
            duration = 1100
            repeatCount = reverse.first
            repeatMode = reverse.second
            start()
        }

        // Aura de neón "respirando"
        ambientAnimations += ObjectAnimator.ofFloat(auraGlow, View.ALPHA, 0.45f, 1f).apply {
            duration = 2400
            repeatCount = reverse.first
            repeatMode = reverse.second
            start()
        }

        // Aura del botón de Ajustes "respirando" con fase desfasada
        ambientAnimations += ObjectAnimator.ofFloat(settingsGlow, View.ALPHA, 0.55f, 1f).apply {
            duration = 2400
            repeatCount = reverse.first
            repeatMode = reverse.second
            startDelay = 1200
            start()
        }

        // Barrido de escaneo descendente
        val screenHeight = resources.displayMetrics.heightPixels
        scanline.translationY = -scanline.height.toFloat()
        ambientAnimations += ObjectAnimator.ofFloat(scanline, View.TRANSLATION_Y, -scanline.height.toFloat(), screenHeight.toFloat()).apply {
            duration = 7000
            repeatCount = invert.first
            repeatMode = invert.second
            start()
        }

        // Barras de ecualizador con alturas distintas y fase desfasada
        val phases = listOf(0, 140, 320, 220, 60)
        eqBars.forEachIndexed { index, bar ->
            val maxScale = when (index) {
                1 -> 2.2f
                3 -> 2.4f
                else -> 1.4f
            }
            ambientAnimations += ObjectAnimator.ofFloat(bar, View.SCALE_Y, 0.35f, maxScale).apply {
                duration = (700 + (index % 3) * 180).toLong()
                repeatCount = invert.first
                repeatMode = invert.second
                startDelay = phases[index].toLong()
                start()
            }
        }
    }

    private fun stopAmbientAnimations() {
        ambientAnimations.forEach { it.cancel() }
        ambientAnimations.clear()
        stopToggleGlowPulse()
        imgStatusDot.alpha = 1f
        auraGlow.alpha = 1f
        settingsGlow.alpha = 1f
        toggleGlow.alpha = 1f
        eqBars.forEach { it.scaleY = 1f }
    }

    override fun onResume() {
        super.onResume()
        if (!didEnterAnimations) {
            didEnterAnimations = true
            playEntranceAnimations()
        }
        startAmbientAnimations()
        // Check if overlay service is running when returning to the activity
        if (Settings.canDrawOverlays(this)) {
            updateStatusMessage("Listo para usar")
        } else {
            updateStatusMessage("Permiso de superposición requerido")
        }
        updateHUDStatus()
        applyNeonAccent()
    }

    override fun onPause() {
        super.onPause()
        stopAmbientAnimations()
    }

    private fun playEntranceAnimations() {
        val icon = findViewById<View>(R.id.imgAppIcon)
        icon.alpha = 0f
        icon.scaleX = 0.55f
        icon.scaleY = 0.55f
        icon.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(600).setInterpolator(
            android.view.animation.DecelerateInterpolator()
        ).start()
    }
}