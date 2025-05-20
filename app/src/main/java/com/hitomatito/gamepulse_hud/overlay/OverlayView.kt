package com.hitomatito.gamepulse_hud.overlay

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.hitomatito.gamepulse_hud.R

@Suppress("unused")
class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
        setBackgroundResource(R.drawable.overlay_background)
        inflate(context, R.layout.overlay_layout, this)
    }

    @Suppress("UNUSED_PARAMETER")
    fun updateMetrics(fps: Int, cpuTemp: Float?, gpuTemp: Float?) {
        // Lógica de actualización
    }
}