# 🎮 GamePulse HUD

[![Android](https://img.shields.io/badge/Android-API%2026+-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**GamePulse HUD** es una aplicación Android avanzada que proporciona un **monitor de rendimiento en tiempo real** mediante una **superposición (overlay)** personalizable. Diseñada especialmente para jugadores y desarrolladores, permite supervisar métricas del sistema como FPS, temperatura de CPU/GPU y uso de memoria directamente sobre cualquier aplicación.

## ✨ Características Principales

### 🔧 **Monitoreo del Sistema**
- **FPS (Frames Por Segundo)**: Monitoreo en tiempo real usando `Choreographer`
- **Temperatura de CPU/GPU**: Lectura de sensores térmicos del sistema
- **Información de Memoria**: RAM total, utilizada y disponible
- **Información de Almacenamiento**: Espacio disponible y utilizado
- **Métricas de la App**: Uso de memoria específico de la aplicación

### 🎨 **Personalización Avanzada**
- **Posicionamiento Libre**: Arrastra el HUD a cualquier parte de la pantalla
- **Estilos de Fuente**: Default, Serif, Sans Serif, Monospace
- **Peso de Fuente**: Normal, Bold, Italic
- **Tamaño de Texto**: Ajustable de 10sp a 60sp
- **Colores Personalizables**: Control RGB completo para el texto
- **Opacidad del Fondo**: Control deslizante de transparencia (0-100%)
- **Bordes Redondeados**: Radio de esquina ajustable
- **Visibilidad Selectiva**: Mostrar/ocultar métricas individuales

### 🚀 **Funcionalidades Técnicas**
- **Servicio en Primer Plano**: Operación continua sin interrupciones
- **Permisos Inteligentes**: Gestión automática de permisos del sistema
- **Persistencia de Configuración**: `SharedPreferences` para guardar ajustes
- **Interfaz Material Design**: UI moderna con tema claro/oscuro
- **Arquitectura Modular**: Separación clara de responsabilidades

### 📱 **Compatibilidad y Rendimiento**
- **API Mínima**: Android 8.0 (API 26)
- **API Objetivo**: Android 14 (API 35)
- **Arquitectura**: Kotlin con AndroidX
- **Rendimiento Optimizado**: Baja latencia y consumo mínimo de recursos

## 🏗️ Arquitectura del Proyecto

```
app/
├── core/
│   ├── SystemInfoManager.kt      # Gestión de información del sistema
│   ├── SystemMetrics.kt          # Monitoreo de FPS y métricas
│   └── TemperatureReader.kt      # Lectura de sensores de temperatura
├── overlay/
│   ├── OverlayService.kt         # Servicio de superposición
│   └── OverlayView.kt           # Vista personalizable del HUD
├── ui/
│   ├── MainActivity.kt           # Actividad principal
│   └── SettingsActivity.kt      # Configuración avanzada
└── utils/
    └── PreferencesManager.kt     # Gestión de preferencias
```

## 🔧 Configuración y Requisitos

### **Requisitos del Sistema**
- **Android Studio**: Koala (2024.1.1) o superior
- **JDK**: OpenJDK 21 (jbr-21)
- **Gradle**: 8.10.0 con Kotlin DSL
- **Android SDK**: Compilación con API 35
- **Dispositivo**: Android 8.0+ (API 26)

### **Permisos Requeridos**
- `SYSTEM_ALERT_WINDOW`: Para mostrar superposiciones sobre otras apps
- `FOREGROUND_SERVICE`: Para mantener el servicio activo
- `POST_NOTIFICATIONS`: Para notificaciones del servicio (Android 13+)
- `FOREGROUND_SERVICE_SPECIAL_USE`: Para servicios especializados (Android 14+)

## 🚀 Instalación y Configuración

### **1. Clonar el Repositorio**
```bash
git clone https://github.com/Hitomatito/GamePulse_HUD.git
cd GamePulse_HUD
```

### **2. Abrir en Android Studio**
```bash
# Abrir Android Studio y seleccionar "Open an Existing Project"
# Navegar hasta la carpeta GamePulse_HUD
```

### **3. Sincronizar Dependencias**
```bash
# Android Studio sincronizará automáticamente las dependencias
# O ejecutar manualmente:
./gradlew build
```

### **4. Ejecutar la Aplicación**
- Conectar dispositivo Android o usar emulador
- Compilar y ejecutar desde Android Studio
- Otorgar permisos necesarios cuando se soliciten

## 📖 Guía de Uso

### **Primera Configuración**
1. **Iniciar la App**: Abrir GamePulse HUD
2. **Otorgar Permisos**: 
   - Permiso de superposición (obligatorio)
   - Permiso de notificaciones (Android 13+)
3. **Activar HUD**: Presionar "🎮 Iniciar HUD"
4. **Posicionar**: Arrastrar el HUD a la posición deseada

### **Personalización Avanzada**
1. **Acceder a Configuración**: Botón "⚙️ Configuraciones"
2. **Ajustar Apariencia**:
   - Tamaño y color del texto
   - Estilo y peso de fuente
   - Opacidad del fondo
   - Radio de bordes
3. **Configurar Métricas**: Seleccionar qué información mostrar
4. **Guardar**: Los cambios se aplican automáticamente

### **Controles del HUD**
- **Arrastrar**: Mover el HUD por la pantalla
- **Posición**: Se guarda automáticamente
- **Visibilidad**: Control independiente de cada métrica

## 🔍 Características Técnicas Detalladas

### **Monitoreo de FPS**
```kotlin
// Implementación usando Choreographer para precisión máxima
class SystemMetrics {
    private val choreographer = Choreographer.getInstance()
    private var frameCount = 0
    
    // Callback ejecutado en cada frame
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            frameCount++
            choreographer.postFrameCallback(this)
        }
    }
}
```

### **Lectura de Temperatura**
```kotlin
// Acceso a sensores térmicos del sistema
class TemperatureReader {
    private val thermalFiles = mapOf(
        "CPU" to arrayOf("/sys/class/thermal/thermal_zone0/temp"),
        "GPU" to arrayOf("/sys/class/thermal/thermal_zone1/temp")
    )
}
```

### **Gestión de Memoria**
```kotlin
// Análisis detallado del uso de memoria
fun getMemoryUsage(): MemoryInfo {
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    return MemoryInfo(/* datos procesados */)
}
```

## 📊 Métricas Disponibles

| Métrica | Descripción | Rango | Unidad |
|---------|-------------|--------|--------|
| **FPS** | Frames por segundo | 0-∞ | fps |
| **CPU Temp** | Temperatura del procesador | 0-100+ | °C |
| **GPU Temp** | Temperatura del procesador gráfico | 0-100+ | °C |
| **RAM** | Memoria utilizada/total | 0-100 | % |
| **Storage** | Almacenamiento disponible | 0-∞ | GB |

## 🎛️ Opciones de Personalización

### **Tipografía**
- **Estilos**: Default, Serif, Sans Serif, Monospace
- **Pesos**: Normal, Bold, Italic
- **Tamaños**: 10sp - 60sp (incrementos de 0.5sp)

### **Colores**
- **Control RGB**: Valores 0-255 para cada canal
- **Vista Previa**: Actualización en tiempo real
- **Presets**: Colores predeterminados disponibles

### **Layout**
- **Posición**: Coordenadas X,Y personalizables
- **Opacidad**: 0-100% para el fondo
- **Bordes**: Radio de 0-50dp
- **Visibilidad**: Toggle independiente por métrica

## 🛠️ Stack Tecnológico

### **Lenguajes y Frameworks**
- **Kotlin**: 2.0.21 (100% del código)
- **Android SDK**: Target API 35, Min API 26
- **AndroidX**: Bibliotecas modernas
- **Material Design**: Components 1.12.0

### **Arquitectura**
- **MVVM**: Separación de vista y lógica
- **Services**: `OverlayService` para funcionamiento continuo
- **SharedPreferences**: Persistencia de configuración
- **WindowManager**: Gestión de superposiciones

### **Dependencias Principales**
```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
}
```

## 📱 Compatibilidad

### **Versiones de Android**
- ✅ **Android 8.0** (API 26) - Versión mínima
- ✅ **Android 9.0** (API 28) - Completamente compatible
- ✅ **Android 10** (API 29) - Completamente compatible
- ✅ **Android 11** (API 30) - Completamente compatible
- ✅ **Android 12** (API 31) - Completamente compatible
- ✅ **Android 13** (API 33) - Con permisos de notificación
- ✅ **Android 14** (API 34) - Con servicios especializados

### **Arquitecturas**
- ✅ **ARM64-v8a** (Dispositivos modernos)
- ✅ **ARMv7** (Dispositivos anteriores)
- ✅ **x86_64** (Emuladores)
- ✅ **x86** (Emuladores antiguos)

## 🚧 Estado del Desarrollo

### **Versión Actual: 1.0.0**
- ✅ Monitoreo básico de FPS
- ✅ Lectura de temperatura CPU/GPU
- ✅ Información de memoria y almacenamiento
- ✅ Personalización completa de UI
- ✅ Persistencia de configuración
- ✅ Servicio en primer plano
- ✅ Gestión de permisos

### **Próximas Características (Roadmap)**
- 🔄 Gráficos de rendimiento en tiempo real
- 🔄 Exportación de datos de monitoreo
- 🔄 Perfiles de configuración
- 🔄 Widgets de escritorio
- 🔄 API para aplicaciones externas

## 🤝 Contribución

¡Las contribuciones son bienvenidas! Por favor:

1. **Fork** el repositorio
2. **Crear** una rama para tu feature (`git checkout -b feature/nueva-caracteristica`)
3. **Commit** tus cambios (`git commit -am 'Agregar nueva característica'`)
4. **Push** a la rama (`git push origin feature/nueva-caracteristica`)
5. **Abrir** un Pull Request

### **Estándares de Código**
- Seguir [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Documentar funciones públicas
- Incluir tests para nuevas características
- Mantener compatibilidad con API mínima

## 📄 Licencia

Este proyecto está licenciado bajo la **Licencia MIT** - ver el archivo [LICENSE](LICENSE) para más detalles.

## 📞 Soporte y Contacto

- **Issues**: [GitHub Issues](https://github.com/Hitomatito/GamePulse_HUD/issues)
- **Documentación**: [Wiki del Proyecto](https://github.com/Hitomatito/GamePulse_HUD/wiki)
- **Desarrollador**: [@Hitomatito](https://github.com/Hitomatito)

## 🙏 Agradecimientos

- **Android Team** por las herramientas de desarrollo
- **Material Design** por los componentes de UI
- **Kotlin Team** por el lenguaje moderno
- **Comunidad Open Source** por las bibliotecas utilizadas

---

<div align="center">

**¿Te gusta GamePulse HUD?** ⭐ ¡Dale una estrella al repositorio!

[📱 Descargar APK](https://github.com/Hitomatito/GamePulse_HUD/releases) | [📖 Documentación](https://github.com/Hitomatito/GamePulse_HUD/wiki) | [🐛 Reportar Bug](https://github.com/Hitomatito/GamePulse_HUD/issues)

</div>
