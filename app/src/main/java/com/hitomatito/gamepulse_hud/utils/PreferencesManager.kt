package com.hitomatito.gamepulse_hud.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "gamepulse_prefs"
        
        // Keys for preferences
        private const val KEY_TEXT_SIZE = "text_size"
        private const val KEY_TEXT_COLOR = "text_color"
        private const val KEY_BACKGROUND_OPACITY = "background_opacity"
        private const val KEY_OVERLAY_POSITION_X = "overlay_position_x"
        private const val KEY_OVERLAY_POSITION_Y = "overlay_position_y"
        private const val KEY_SHOW_FPS = "show_fps"
        private const val KEY_SHOW_CPU_TEMP = "show_cpu_temp"
        private const val KEY_SHOW_GPU_TEMP = "show_gpu_temp"
        private const val KEY_CORNER_RADIUS = "corner_radius"
        private const val KEY_OVERLAY_WIDTH = "overlay_width"
        private const val KEY_OVERLAY_HEIGHT = "overlay_height"
        private const val KEY_FONT_STYLE_INDEX = "font_style_index"
        private const val KEY_FONT_WEIGHT_INDEX = "font_weight_index"
        
        // Default values
        const val DEFAULT_TEXT_SIZE = 16f
        const val DEFAULT_TEXT_COLOR = 0xFFFFFFFF.toInt() // White
        const val DEFAULT_BACKGROUND_OPACITY = 0.8f
        const val DEFAULT_OVERLAY_POSITION_X = 0
        const val DEFAULT_OVERLAY_POSITION_Y = 0
        const val DEFAULT_CORNER_RADIUS = 8f
        const val DEFAULT_OVERLAY_WIDTH = 200
        const val DEFAULT_OVERLAY_HEIGHT = 150
        const val DEFAULT_FONT_STYLE_INDEX = 0 // Default
        const val DEFAULT_FONT_WEIGHT_INDEX = 0 // Normal
    }

    // Text size
    var textSize: Float
        get() = sharedPreferences.getFloat(KEY_TEXT_SIZE, DEFAULT_TEXT_SIZE)
        set(value) = sharedPreferences.edit { putFloat(KEY_TEXT_SIZE, value) }

    // Text color
    var textColor: Int
        get() = sharedPreferences.getInt(KEY_TEXT_COLOR, DEFAULT_TEXT_COLOR)
        set(value) = sharedPreferences.edit { putInt(KEY_TEXT_COLOR, value) }

    // Background opacity
    var backgroundOpacity: Float
        get() = sharedPreferences.getFloat(KEY_BACKGROUND_OPACITY, DEFAULT_BACKGROUND_OPACITY)
        set(value) = sharedPreferences.edit { putFloat(KEY_BACKGROUND_OPACITY, value) }

    // Overlay position
    var overlayPositionX: Int
        get() = sharedPreferences.getInt(KEY_OVERLAY_POSITION_X, DEFAULT_OVERLAY_POSITION_X)
        set(value) = sharedPreferences.edit { putInt(KEY_OVERLAY_POSITION_X, value) }

    var overlayPositionY: Int
        get() = sharedPreferences.getInt(KEY_OVERLAY_POSITION_Y, DEFAULT_OVERLAY_POSITION_Y)
        set(value) = sharedPreferences.edit { putInt(KEY_OVERLAY_POSITION_Y, value) }

    // Display options
    var showFps: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_FPS, true)
        set(value) = sharedPreferences.edit { putBoolean(KEY_SHOW_FPS, value) }

    var showCpuTemp: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_CPU_TEMP, true)
        set(value) = sharedPreferences.edit { putBoolean(KEY_SHOW_CPU_TEMP, value) }

    var showGpuTemp: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_GPU_TEMP, true)
        set(value) = sharedPreferences.edit { putBoolean(KEY_SHOW_GPU_TEMP, value) }

    // Corner radius
    var cornerRadius: Float
        get() = sharedPreferences.getFloat(KEY_CORNER_RADIUS, DEFAULT_CORNER_RADIUS)
        set(value) = sharedPreferences.edit { putFloat(KEY_CORNER_RADIUS, value) }

    // Overlay dimensions
    var overlayWidth: Int
        get() = sharedPreferences.getInt(KEY_OVERLAY_WIDTH, DEFAULT_OVERLAY_WIDTH)
        set(value) = sharedPreferences.edit { putInt(KEY_OVERLAY_WIDTH, value) }

    var overlayHeight: Int
        get() = sharedPreferences.getInt(KEY_OVERLAY_HEIGHT, DEFAULT_OVERLAY_HEIGHT)
        set(value) = sharedPreferences.edit { putInt(KEY_OVERLAY_HEIGHT, value) }

    // Font settings
    var fontStyleIndex: Int
        get() = sharedPreferences.getInt(KEY_FONT_STYLE_INDEX, DEFAULT_FONT_STYLE_INDEX)
        set(value) = sharedPreferences.edit { putInt(KEY_FONT_STYLE_INDEX, value) }

    var fontWeightIndex: Int
        get() = sharedPreferences.getInt(KEY_FONT_WEIGHT_INDEX, DEFAULT_FONT_WEIGHT_INDEX)
        set(value) = sharedPreferences.edit { putInt(KEY_FONT_WEIGHT_INDEX, value) }

    fun resetToDefaults() {
        textSize = DEFAULT_TEXT_SIZE
        textColor = DEFAULT_TEXT_COLOR
        backgroundOpacity = DEFAULT_BACKGROUND_OPACITY
        overlayPositionX = DEFAULT_OVERLAY_POSITION_X
        overlayPositionY = DEFAULT_OVERLAY_POSITION_Y
        showFps = true
        showCpuTemp = true
        showGpuTemp = true
        cornerRadius = DEFAULT_CORNER_RADIUS
        overlayWidth = DEFAULT_OVERLAY_WIDTH
        overlayHeight = DEFAULT_OVERLAY_HEIGHT
        fontStyleIndex = DEFAULT_FONT_STYLE_INDEX
        fontWeightIndex = DEFAULT_FONT_WEIGHT_INDEX
    }
}
