package com.hitomatito.gamepulse_hud.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Gráfica de línea con el historial de FPS del HUD.
 * Mantiene una ventana deslizante de las últimas muestras y la dibuja
 * con Canvas, con autoescala del eje Y y relleno degradado bajo la línea.
 */
class MetricsChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val maxSamples = 90
    private val samples = ArrayDeque<Float>()

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(2f)
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1f)
        color = Color.WHITE
        alpha = 30
    }

    private val linePath = Path()
    private val fillPath = Path()
    private var maxValue = 60f
    private var chartColor = Color.WHITE

    fun setChartColor(color: Int) {
        chartColor = color
        linePaint.color = color
        fillPaint.color = Color.argb(40, Color.red(color), Color.green(color), Color.blue(color))
        invalidate()
    }

    /** Añade una muestra (FPS) y redibuja. Se llama desde el hilo principal. */
    fun addSample(value: Float) {
        samples.addLast(value)
        while (samples.size > maxSamples) {
            samples.removeFirst()
        }
        // Autoescala: el máximo se redondea hacia arriba al múltiplo de 10 más cercano.
        val max = samples.maxOrNull() ?: return
        maxValue = maxOf(30f, ((max + 9f) / 10f).toInt() * 10f)
        postInvalidateOnAnimation()
    }

    fun clear() {
        samples.clear()
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val padY = dp(3f)
        val gridLines = 3
        for (i in 0..gridLines) {
            val y = padY + (h - 2 * padY) * i / gridLines
            canvas.drawLine(0f, y, w, y, gridPaint)
        }

        val count = samples.size
        if (count == 0) return

        val step = w / (maxSamples - 1)
        val startX = (maxSamples - count) * step

        linePath.reset()
        var index = 0
        var lastX = startX
        var lastY = 0f
        for (value in samples) {
            val x = startX + index * step
            val y = padY + (h - 2 * padY) * (1f - (value / maxValue).coerceIn(0f, 1f))
            if (index == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
            lastX = x
            lastY = y
            index++
        }

        if (count == 1) {
            canvas.drawCircle(lastX, lastY, dp(3f), linePaint)
        } else {
            // Relleno degradado contenido bajo la línea.
            fillPath.set(linePath)
            fillPath.lineTo((maxSamples - 1) * step, h)
            fillPath.lineTo(startX, h)
            fillPath.close()
            canvas.drawPath(fillPath, fillPaint)
            canvas.drawPath(linePath, linePaint)
        }
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}