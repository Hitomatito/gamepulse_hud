package com.hitomatito.gamepulse_hud.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.hitomatito.gamepulse_hud.R

/**
 * Toasts con la estética gamer del proyecto.
 * Reutiliza la última instancia para encolar correctamente los mensajes.
 */
object GamerToast {

    private var last: Toast? = null

    fun show(context: Context, message: String, longDuration: Boolean = false) {
        last?.cancel()

        val view = LayoutInflater.from(context).inflate(R.layout.toast_gamer, null)
        view.findViewById<TextView>(R.id.toastMessage).text = message

        last = Toast(context).apply {
            duration = if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 110.dp(context))
            this.view = view
            show()
        }
    }
}

private fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()