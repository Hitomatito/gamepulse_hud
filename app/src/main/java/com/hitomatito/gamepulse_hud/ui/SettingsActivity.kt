package com.hitomatito.gamepulse_hud.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hitomatito.gamepulse_hud.R
import com.hitomatito.gamepulse_hud.overlay.OverlayService
import com.hitomatito.gamepulse_hud.utils.PreferencesManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager

    // UI Components
    private lateinit var textSizeSeekBar: SeekBar
    private lateinit var textSizeLabel: TextView
    private lateinit var backgroundOpacitySeekBar: SeekBar
    private lateinit var backgroundOpacityLabel: TextView
    private lateinit var cornerRadiusSeekBar: SeekBar
    private lateinit var cornerRadiusLabel: TextView

    // Nuevos componentes para fuentes
    private lateinit var fontStyleSpinner: Spinner
    private lateinit var fontWeightSpinner: Spinner
    private lateinit var fontSampleText: TextView

    private lateinit var colorRedSeekBar: SeekBar
    private lateinit var colorGreenSeekBar: SeekBar
    private lateinit var colorBlueSeekBar: SeekBar
    private lateinit var colorPreview: View

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var showFpsSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var showCpuTempSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var showGpuTempSwitch: Switch

    private lateinit var positionXSeekBar: SeekBar
    private lateinit var positionYSeekBar: SeekBar
    private lateinit var positionXLabel: TextView
    private lateinit var positionYLabel: TextView

    private lateinit var resetButton: Button

    // Arrays para los estilos de fuente
    private val fontStyles = arrayOf("Default", "Serif", "Sans Serif", "Monospace")
    private val fontWeights = arrayOf("Normal", "Bold", "Italic")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferencesManager = PreferencesManager(this)
        initializeViews()
        setupListeners()
        loadCurrentSettings()
    }

    private fun initializeViews() {
        // Text size
        textSizeSeekBar = findViewById(R.id.seekBarTextSize)
        textSizeLabel = findViewById(R.id.tvTextSizeValue)

        // Background opacity
        backgroundOpacitySeekBar = findViewById(R.id.seekBarBackgroundOpacity)
        backgroundOpacityLabel = findViewById(R.id.tvBackgroundOpacityValue)

        // Corner radius
        cornerRadiusSeekBar = findViewById(R.id.seekBarCornerRadius)
        cornerRadiusLabel = findViewById(R.id.tvCornerRadiusValue)

        // Nuevos componentes de fuente
        fontStyleSpinner = findViewById(R.id.spinnerFontStyle)
        fontWeightSpinner = findViewById(R.id.spinnerFontWeight)
        fontSampleText = findViewById(R.id.tvFontSample)

        // Configurar spinners
        setupFontSpinners()

        // Color controls
        colorRedSeekBar = findViewById(R.id.seekBarColorRed)
        colorGreenSeekBar = findViewById(R.id.seekBarColorGreen)
        colorBlueSeekBar = findViewById(R.id.seekBarColorBlue)
        colorPreview = findViewById<View>(R.id.viewColorPreview)

        // Display switches
        showFpsSwitch = findViewById(R.id.switchShowFps)
        showCpuTempSwitch = findViewById(R.id.switchShowCpuTemp)
        showGpuTempSwitch = findViewById(R.id.switchShowGpuTemp)

        // Position controls
        positionXSeekBar = findViewById(R.id.seekBarPositionX)
        positionYSeekBar = findViewById(R.id.seekBarPositionY)
        positionXLabel = findViewById(R.id.tvPositionXValue)
        positionYLabel = findViewById(R.id.tvPositionYValue)

        // Reset button
        resetButton = findViewById(R.id.btnReset)
    }

    @SuppressLint("SetTextI18n")
    private fun setupFontSpinners() {
        // Configurar spinner de estilos de fuente
        val fontStyleAdapter = ArrayAdapter(this, R.layout.spinner_item, fontStyles)
        fontStyleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        fontStyleSpinner.adapter = fontStyleAdapter

        // Configurar spinner de peso de fuente
        val fontWeightAdapter = ArrayAdapter(this, R.layout.spinner_item, fontWeights)
        fontWeightAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        fontWeightSpinner.adapter = fontWeightAdapter

        // Configurar texto de muestra
        fontSampleText.text = "FPS: 60 | CPU: 45°C | GPU: 55°C"
    }

    private fun setupListeners() {
        // Text size
        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val textSize = 10f + progress.toFloat() / 2f // Range: 10-60sp
                textSizeLabel.text = "${textSize.toInt()}sp"
                updateFontSample()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSettings()
            }
        })

        // Font style spinner
        fontStyleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateFontSample()
                saveSettings()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Font weight spinner
        fontWeightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateFontSample()
                saveSettings()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Background opacity
        backgroundOpacitySeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val opacity = progress / 100f
                backgroundOpacityLabel.text = "${(opacity * 100).toInt()}%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSettings()
            }
        })

        // Corner radius
        cornerRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                cornerRadiusLabel.text = "${progress}dp"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSettings()
            }
        })

        // Color seekbars
        val colorChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateColorPreview()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSettings()
            }
        }

        colorRedSeekBar.setOnSeekBarChangeListener(colorChangeListener)
        colorGreenSeekBar.setOnSeekBarChangeListener(colorChangeListener)
        colorBlueSeekBar.setOnSeekBarChangeListener(colorChangeListener)

        // Switches
        showFpsSwitch.setOnCheckedChangeListener { _, _ -> saveSettings() }
        showCpuTempSwitch.setOnCheckedChangeListener { _, _ -> saveSettings() }
        showGpuTempSwitch.setOnCheckedChangeListener { _, _ -> saveSettings() }

        // Position
        positionXSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                positionXLabel.text = "${progress}px"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSettings()
            }
        })

        positionYSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                positionYLabel.text = "${progress}px"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSettings()
            }
        })

        // Buttons
        resetButton.setOnClickListener { resetSettings() }
    }

    @SuppressLint("SetTextI18n")
    private fun loadCurrentSettings() {
        // Text size (10-60sp range)
        val currentTextSize = preferencesManager.textSize
        textSizeSeekBar.progress = ((currentTextSize - 10f) * 2f).toInt()
        textSizeLabel.text = "${currentTextSize.toInt()}sp"

        // Background opacity
        val currentOpacity = preferencesManager.backgroundOpacity
        backgroundOpacitySeekBar.progress = (currentOpacity * 100).toInt()
        backgroundOpacityLabel.text = "${(currentOpacity * 100).toInt()}%"

        // Corner radius
        val currentRadius = preferencesManager.cornerRadius
        cornerRadiusSeekBar.progress = currentRadius.toInt()
        cornerRadiusLabel.text = "${currentRadius.toInt()}dp"

        // Color
        val currentColor = preferencesManager.textColor
        colorRedSeekBar.progress = Color.red(currentColor)
        colorGreenSeekBar.progress = Color.green(currentColor)
        colorBlueSeekBar.progress = Color.blue(currentColor)
        updateColorPreview()

        // Display options
        showFpsSwitch.isChecked = preferencesManager.showFps
        showCpuTempSwitch.isChecked = preferencesManager.showCpuTemp
        showGpuTempSwitch.isChecked = preferencesManager.showGpuTemp

        // Position
        positionXSeekBar.progress = preferencesManager.overlayPositionX
        positionYSeekBar.progress = preferencesManager.overlayPositionY
        positionXLabel.text = "${preferencesManager.overlayPositionX}px"
        positionYLabel.text = "${preferencesManager.overlayPositionY}px"
        // Font settings
        fontStyleSpinner.setSelection(preferencesManager.fontStyleIndex)
        fontWeightSpinner.setSelection(preferencesManager.fontWeightIndex)

        // Update sample text with current font settings
        updateFontSample()
    }

    private fun updateColorPreview() {
        val red = colorRedSeekBar.progress
        val green = colorGreenSeekBar.progress
        val blue = colorBlueSeekBar.progress
        val color = Color.rgb(red, green, blue)
        colorPreview.setBackgroundColor(color)
        // No se llama a updateFontSample aquí para evitar ciclos
    }

    private fun updateFontSample() {
        val selectedStyle = fontStyleSpinner.selectedItemPosition
        val selectedWeight = fontWeightSpinner.selectedItemPosition
        val textSize = 10f + textSizeSeekBar.progress.toFloat() / 2f

        // Aplicar tamaño
        fontSampleText.textSize = textSize

        // Aplicar estilo de fuente
        val typeface = when (selectedStyle) {
            1 -> Typeface.SERIF
            2 -> Typeface.SANS_SERIF
            3 -> Typeface.MONOSPACE
            else -> Typeface.DEFAULT
        }

        // Aplicar peso de fuente
        val style = when (selectedWeight) {
            1 -> Typeface.BOLD
            2 -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }

        fontSampleText.typeface = Typeface.create(typeface, style)
    }

    private fun saveSettings() {
        // Save text size
        val textSize = 10f + textSizeSeekBar.progress.toFloat() / 2f
        preferencesManager.textSize = textSize

        // Save background opacity
        preferencesManager.backgroundOpacity = backgroundOpacitySeekBar.progress / 100f

        // Save corner radius
        preferencesManager.cornerRadius = cornerRadiusSeekBar.progress.toFloat()

        // Save color
        val red = colorRedSeekBar.progress
        val green = colorGreenSeekBar.progress
        val blue = colorBlueSeekBar.progress
        preferencesManager.textColor = Color.rgb(red, green, blue)

        // Save display options
        preferencesManager.showFps = showFpsSwitch.isChecked
        preferencesManager.showCpuTemp = showCpuTempSwitch.isChecked
        preferencesManager.showGpuTemp = showGpuTempSwitch.isChecked

        // Save position
        preferencesManager.overlayPositionX = positionXSeekBar.progress
        preferencesManager.overlayPositionY = positionYSeekBar.progress
        // Save font settings
        preferencesManager.fontStyleIndex = fontStyleSpinner.selectedItemPosition
        preferencesManager.fontWeightIndex = fontWeightSpinner.selectedItemPosition

        // Restart overlay service to apply changes
        restartOverlayService()
    }

    private fun resetSettings() {
        preferencesManager.resetToDefaults()
        loadCurrentSettings()
        Toast.makeText(this, "Configuración restablecida", Toast.LENGTH_SHORT).show()
        restartOverlayService()
    }

    @SuppressLint("ImplicitSamInstance")
    private fun restartOverlayService() {
        // Stop current service
        stopService(Intent(this, OverlayService::class.java))

        // Start service again with new settings
        startService(Intent(this, OverlayService::class.java))
    }
}
