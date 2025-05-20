package com.hitomatito.gamepulse_hud.core

import android.os.Handler
import android.os.Looper
import android.view.Choreographer

class SystemMetrics { // Cambiado de object a class
    private var frameCount = 0
    private var fps = 0
    private lateinit var choreographer: Choreographer
    private val handler = Handler(Looper.getMainLooper())
    private var fpsCallback: ((fps: Int) -> Unit)? = null

    private val frameCallbackInstance = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            frameCount++
            // Volver a postear el callback para la siguiente trama
            if (::choreographer.isInitialized) { // Asegurarse de que choreographer esté inicializado
                 choreographer.postFrameCallback(this)
            }
        }
    }

    private val fpsRunnable = object : Runnable {
        override fun run() {
            fps = frameCount
            frameCount = 0
            fpsCallback?.invoke(fps)
            handler.postDelayed(this, 1000)
        }
    }

    fun startFpsMonitoring(callback: (fps: Int) -> Unit) {
        this.fpsCallback = callback
        // Asegurarse de que Choreographer se obtenga en el hilo correcto (normalmente el principal)
        // y que las callbacks se posteen desde ese hilo.
        // Si startFpsMonitoring se llama desde un hilo que no es el principal,
        // obtener la instancia de Choreographer puede fallar o comportarse inesperadamente.
        // Lo más seguro es asegurar que se llame desde el hilo principal o usar un Handler para ello.
        handler.post {
            choreographer = Choreographer.getInstance()
            choreographer.postFrameCallback(frameCallbackInstance)
            // Iniciar el runnable que calcula FPS
            handler.post(fpsRunnable)
        }
    }

    fun stopMonitoring() {
        handler.removeCallbacks(fpsRunnable)
        if (::choreographer.isInitialized) {
            // Asegurarse de que removeFrameCallback se llame en el mismo hilo que postFrameCallback
            handler.post { // O directamente si se garantiza que stopMonitoring se llama desde el hilo principal
                choreographer.removeFrameCallback(frameCallbackInstance)
            }
        }
        fpsCallback = null
    }
}