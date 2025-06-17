package com.hitomatito.gamepulse_hud.overlay

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.hitomatito.gamepulse_hud.R
import com.hitomatito.gamepulse_hud.utils.PreferencesManager

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val preferencesManager = PreferencesManager(context)
    private lateinit var txtFPS: TextView
    private lateinit var txtCPU: TextView
    private lateinit var txtGPU: TextView
    private lateinit var fpsContainer: View
    private lateinit var cpuContainer: View
    private lateinit var gpuContainer: View
    
    // Variables para el manejo de toques
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    init {
        orientation = VERTICAL
        inflate(context, R.layout.overlay_layout, this)
        initializeViews()
        applyCustomizations()
        setupTouchListener()
    }

    private fun initializeViews() {
        txtFPS = findViewById(R.id.txtFPS)
        txtCPU = findViewById(R.id.txtCPU)
        txtGPU = findViewById(R.id.txtGPU)
        fpsContainer = findViewById(R.id.fps_container)
        cpuContainer = findViewById(R.id.cpu_container)
        gpuContainer = findViewById(R.id.gpu_container)
    }

    private fun applyCustomizations() {
        // Apply text customizations
        val textViews = listOf(txtFPS, txtCPU, txtGPU)

        // Get font style and weight from preferences
        val fontStyleIndex = preferencesManager.fontStyleIndex
        val fontWeightIndex = preferencesManager.fontWeightIndex

        // Apply font style
        val typeface = when (fontStyleIndex) {
            1 -> Typeface.SERIF
            2 -> Typeface.SANS_SERIF
            3 -> Typeface.MONOSPACE
            else -> Typeface.DEFAULT
        }

        // Apply font weight
        val style = when (fontWeightIndex) {
            1 -> Typeface.BOLD
            2 -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }

        val finalTypeface = Typeface.create(typeface, style)

        textViews.forEach { textView ->
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, preferencesManager.textSize)
            textView.setTextColor(preferencesManager.textColor)
            textView.typeface = finalTypeface
        }

        // Apply visibility settings
        fpsContainer.visibility = if (preferencesManager.showFps) View.VISIBLE else View.GONE
        cpuContainer.visibility = if (preferencesManager.showCpuTemp) View.VISIBLE else View.GONE
        gpuContainer.visibility = if (preferencesManager.showGpuTemp) View.VISIBLE else View.GONE

        // Apply background customization
        updateBackground()
    }

    private fun updateBackground() {
        val opacity = preferencesManager.backgroundOpacity
        
        // Si la opacidad es muy baja o 0, quitar completamente el fondo
        if (opacity <= 0.05f) {
            this.background = null
            return
        }
        
        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = preferencesManager.cornerRadius * context.resources.displayMetrics.density
            
            // Calculate alpha from opacity (0.0-1.0) to alpha (0-255)
            val alpha = (opacity * 255).toInt().coerceIn(15, 255) // Mínimo alpha de 15 para que sea visible
            
            // Crear gradiente sutil similar al drawable original pero con opacidad controlada
            val startColor = (alpha shl 24) or 0x000000 // Negro con alpha
            val centerColor = ((alpha * 0.85f).toInt() shl 24) or 0x000000
            val endColor = ((alpha * 0.7f).toInt() shl 24) or 0x000000
            
            colors = intArrayOf(startColor, centerColor, endColor)
            gradientType = GradientDrawable.LINEAR_GRADIENT
            orientation = GradientDrawable.Orientation.TL_BR
            
            // Borde sutil con baja opacidad
            setStroke(
                1,
                ((opacity * 64).toInt() shl 24) or 0xFFFFFF // Blanco con alpha proporcional
            )
        }
        
        this.background = background
    }

    fun updateMetrics(fps: Int, cpuTemp: Float?, gpuTemp: Float?) {
        if (preferencesManager.showFps) {
            txtFPS.text = "FPS: $fps"
        }
        if (preferencesManager.showCpuTemp) {
            txtCPU.text = "CPU: ${cpuTemp?.let { "%.1f°C".format(it) } ?: "N/A"}"        }
        if (preferencesManager.showGpuTemp) {
            txtGPU.text = "GPU: ${gpuTemp?.let { "%.1f°C".format(it) } ?: "N/A"}"
        }
    }

    fun setWindowManager(windowManager: WindowManager, layoutParams: WindowManager.LayoutParams) {
        this.windowManager = windowManager
        this.layoutParams = layoutParams
    }

    private fun setupTouchListener() {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams?.x ?: 0
                    initialY = layoutParams?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (windowManager != null && layoutParams != null) {
                        layoutParams?.x = initialX + (event.rawX - initialTouchX).toInt()
                        layoutParams?.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(this, layoutParams)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Guardar la nueva posición en las preferencias
                    layoutParams?.let { params ->
                        preferencesManager.overlayPositionX = params.x
                        preferencesManager.overlayPositionY = params.y
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun refreshCustomizations() {
        applyCustomizations()
    }
}