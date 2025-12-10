# 🏋️‍♂️ TimerFit Pro

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Room](https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-FF6B6B?style=for-the-badge&logo=dagger&logoColor=white)

**El temporizador definitivo para entrenamientos funcionales, fuerza, HIIT y rutinas personalizadas.**

[![Version](https://img.shields.io/badge/version-1.0-blue.svg)](https://github.com/DonGeeo87/TimerFit-Pro)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-green.svg)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-orange.svg)](https://developer.android.com/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)](LICENSE)

[Características](#-características-principales) • [Instalación](#-instalación) • [Arquitectura](#-arquitectura) • [Para Estudiantes](#-para-estudiantes-trainee)

</div>

---

## 📱 Descripción

**TimerFit Pro** es una aplicación Android profesional desarrollada con **Kotlin 2.x + Jetpack Compose + ViewModel + Room**, diseñada para ser el temporizador más potente, visual y profesional para entrenamientos de gimnasio.

### 🎯 Problema que Resuelve

La app resuelve el problema clásico de muchas apps de temporizador: **el temporizador se reinicia al rotar la pantalla o al minimizar la app**. Con arquitectura moderna y persistencia de estado, TimerFit Pro garantiza que el usuario **nunca pierda el progreso del temporizador**, sin importar qué pase con la app.

---

## ✨ Características Principales

### ⭐ Temporizador PRO

- **🔄 Cronómetro circular animado** en 360° con transiciones fluidas
- **🎨 Colores dinámicos** que cambian según el tiempo restante:
  - 🟢 Verde (inicio)
  - 🟡 Amarillo (medio)
  - 🔴 Rojo (final)
- **💓 Efecto "pulse"** en los últimos segundos
- **🔢 Números enormes y legibles** incluso durante entrenamientos intensos
- **📳 Vibración** al iniciar y en los últimos 5 segundos
- **🎛️ Modos de entrenamiento:**
  - ⏱️ **Tiempo fijo** (15s, 30s, 45s, 60s o personalizado)
  - 🔥 **HIIT / Tabata** (trabajo-descanso-rondas)
  - ⬆️ **Conteo ascendente** (cronómetro)
  - 💪 **Series** para entrenamiento de fuerza

### 🏋️‍♂️ Selección de Ejercicios

- **📚 Catálogo completo** de ejercicios por grupo muscular
- **🎯 Categorías:**
  - 💪 Multiarticulares (Sentadilla, Press banca, Peso muerto)
  - 🎯 Aislamiento (Curl, Extensión de tríceps)
  - 🏃 Cardio (Caminadora, Bicicleta, Remo)
  - 🧘 Core (Plancha, Crunch)
  - 🤸 Flexibilidad (Estiramientos)
- **🔍 Búsqueda y filtros** por categoría
- **📊 Información detallada:** músculos trabajados, dificultad, equipamiento

### 📊 Registro Automático

- **💾 Guarda automáticamente** cada sesión completada
- **📝 Almacena:**
  - Ejercicio realizado
  - Duración total
  - Modo de temporizador
  - Fecha y hora
  - Rondas (en modo HIIT)
- **🚫 Sistema anti-duplicados** inteligente

### 📅 Historial Inteligente

- **📊 Estadísticas generales:**
  - ⏱️ Tiempo total entrenado
  - 🏋️ Total de sesiones
  - 💪 Ejercicios únicos realizados
- **📆 Vista agrupada por día**
- **📈 Resumen de sesiones y tiempo total**
- **🔽 Detalles expandibles** por fecha

### 🎯 Rutinas Predefinidas

- **👶 Principiante** (Full Body)
- **🚀 Avanzado** (Torso/Piernas)
- **💪 Ganar Masa** (5×5 strength)
- **🔥 Definición** (HIIT + cardio)

---

## 🧱 Stack Tecnológico

<div align="center">

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| **Lenguaje** | Kotlin | 2.0.21 |
| **UI** | Jetpack Compose | Latest |
| **Arquitectura** | MVVM + Clean Architecture | - |
| **Estado** | ViewModel + StateFlow | - |
| **Base de Datos** | Room | Latest |
| **DI** | Hilt | Latest |
| **Async** | Coroutines + Flow | Latest |
| **UI Framework** | Material Design 3 | Latest |
| **Navegación** | Navigation Compose | Latest |

</div>

### 📦 Dependencias Principales

```kotlin
// UI
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose")

// Arquitectura
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
implementation("androidx.lifecycle:lifecycle-runtime-compose")

// Base de Datos
implementation("androidx.room:room-runtime")
kapt("androidx.room:room-compiler")

// DI
implementation("com.google.dagger:hilt-android")
kapt("com.google.dagger:hilt-compiler")
```

---

## 🏗️ Arquitectura

El proyecto sigue **Arquitectura Limpia (Clean Architecture)** con separación en capas:

```
app/
├── presentation/          # Capa de Presentación
│   ├── screen/           # Pantallas Compose
│   │   ├── TimerScreen.kt
│   │   ├── ExerciseSelectionScreen.kt
│   │   ├── HistoryScreen.kt
│   │   └── RoutinesScreen.kt
│   ├── viewmodel/        # ViewModels (MVVM)
│   │   ├── TimerViewModel.kt
│   │   ├── ExerciseViewModel.kt
│   │   └── HistoryViewModel.kt
│   ├── navigation/       # Navegación
│   │   └── NavGraph.kt
│   └── theme/           # Tema y estilos
│       └── Theme.kt
│
├── domain/               # Capa de Dominio (Lógica de Negocio)
│   ├── model/           # Modelos de dominio
│   │   ├── Exercise.kt
│   │   ├── ExerciseSession.kt
│   │   └── TimerState.kt
│   └── timer/           # TimerEngine (motor del temporizador)
│       └── TimerEngine.kt
│
├── data/                 # Capa de Datos
│   ├── local/
│   │   ├── entity/      # Entidades Room
│   │   │   ├── ExerciseEntity.kt
│   │   │   └── ExerciseSessionEntity.kt
│   │   ├── dao/         # DAOs (Data Access Objects)
│   │   │   ├── ExerciseDao.kt
│   │   │   └── ExerciseSessionDao.kt
│   │   └── database/    # Base de datos Room
│   │       └── TimerFitDatabase.kt
│   └── repository/      # Repositorios
│       ├── ExerciseRepository.kt
│       └── ExerciseSessionRepository.kt
│
└── di/                   # Módulos Hilt (Dependency Injection)
    ├── DatabaseModule.kt
    └── TimerFitApplication.kt
```

### 🔄 Flujo de Datos

```
UI (Compose) 
    ↓
ViewModel (Estado)
    ↓
Repository (Abstracción)
    ↓
DAO (Room)
    ↓
Database (SQLite)
```

---

## 🔑 Características Técnicas Clave

### ✅ Persistencia del Temporizador

- **ViewModel** mantiene el estado del temporizador
- **TimerEngine** es un motor independiente y reutilizable
- **Pausa automática** en `onStop()` del ciclo de vida
- **Reanudación inteligente** en `onResume()`
- **Logs completos** del ciclo de vida en Logcat

### 🔄 Ciclo de Vida Completo

La app implementa y registra todos los métodos del ciclo de vida:

| Método | Descripción | Acción |
|--------|-------------|--------|
| `onCreate()` | Inicialización | Poblar base de datos |
| `onStart()` | App visible | Log de estado |
| `onResume()` | App activa | Reanudar timer si corresponde |
| `onPause()` | App en pausa | Log de estado |
| `onStop()` | App oculta | **Pausar temporizador** |
| `onDestroy()` | Limpieza | Limpiar recursos |

### 🛡️ Sistema Anti-Duplicados

- **ID único por sesión** generado al iniciar el timer
- **Set de IDs guardados** para prevenir duplicados
- **Verificación antes de guardar** en base de datos

---

## 📦 Instalación

### Prerrequisitos

- **Android Studio** Ígnea (2024.1.1) o superior
- **JDK 11** o superior
- **Android SDK** API 24+ (Android 7.0)
- **Dispositivo físico** o **Emulador** con API 24+

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/DonGeeo87/TimerFit-Pro.git
   cd TimerFit-Pro
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar carpeta del proyecto

3. **Sincronizar dependencias**
   - Android Studio hará Gradle Sync automáticamente
   - O manualmente: File → Sync Project with Gradle Files

4. **Ejecutar la app**
   - Conectar dispositivo físico o iniciar emulador
   - Click en Run (▶️) o `Shift + F10`

---

## 🚀 Uso

### Flujo Básico

1. **📱 Abrir la app** → Pantalla de selección de ejercicios
2. **🏋️ Seleccionar ejercicio** desde el catálogo
3. **⏱️ Elegir modo de temporizador** (fijo, HIIT, etc.)
4. **▶️ Iniciar entrenamiento** con el temporizador visual
5. **💾 Registro automático** al finalizar
6. **📊 Ver historial** de entrenamientos diarios

### Modos de Temporizador

#### ⏱️ Tiempo Fijo
- Selecciona duración (15s, 30s, 45s, 60s)
- Cuenta regresiva visual
- Vibración en últimos 5 segundos

#### 🔥 HIIT / Tabata
- Configura tiempo de trabajo
- Configura tiempo de descanso
- Define número de rondas
- Alterna automáticamente entre trabajo y descanso

#### ⬆️ Conteo Ascendente
- Cronómetro que cuenta hacia arriba
- Útil para entrenamientos de tiempo libre

---

## 🎓 Para Estudiantes Trainee

### ¿Qué es este proyecto?

Este es un **proyecto de aprendizaje** que demuestra cómo construir una app Android profesional usando las mejores prácticas y tecnologías modernas.

### 🎯 Conceptos que Aprenderás

#### 1. **Arquitectura MVVM**
```
Model → ViewModel → View
```
- **Model**: Datos (Room Database)
- **ViewModel**: Lógica y estado
- **View**: UI (Jetpack Compose)

#### 2. **Jetpack Compose**
- **Declarativo**: Describes QUÉ quieres, no CÓMO
- **Recomposición**: UI se actualiza automáticamente
- **Estado**: `remember`, `mutableStateOf`, `StateFlow`

#### 3. **ViewModel**
- **Persistencia**: Sobrevive a rotaciones de pantalla
- **Estado**: Mantiene datos de la UI
- **Ciclo de vida**: Vive más que la Activity

#### 4. **Room Database**
- **SQLite** simplificado
- **Entidades**: Tablas
- **DAOs**: Consultas
- **Repositorios**: Abstracción de datos

#### 5. **Hilt (Dependency Injection)**
- **Inyección automática** de dependencias
- **Menos código** repetitivo
- **Testing** más fácil

#### 6. **Coroutines y Flow**
- **Asíncrono**: No bloquea el hilo principal
- **Flow**: Streams de datos reactivos
- **StateFlow**: Estado observable

### 📚 Estructura del Código para Estudiantes

#### Ejemplo: TimerViewModel

```kotlin
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val sessionRepository: ExerciseSessionRepository
) : ViewModel() {
    
    // Estado observable
    val timerState: StateFlow<TimerState> = timerEngine.state
    
    // Función para iniciar timer
    fun startFixedTime(durationMillis: Long) {
        timerEngine.startFixedTime(durationMillis)
    }
    
    // Función para guardar sesión
    fun saveSession() {
        // Lógica de guardado
    }
}
```

#### Ejemplo: TimerScreen (Compose)

```kotlin
@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    // Observar estado
    val timerState by viewModel.timerState.collectAsState()
    
    // UI
    Column {
        Text("${timerState.timeLeftMillis}")
        Button(onClick = { viewModel.startFixedTime(60000) }) {
            Text("Iniciar")
        }
    }
}
```

### 🔍 Puntos Clave para Entender

1. **ViewModel sobrevive a rotaciones** → El timer no se reinicia
2. **StateFlow emite cambios** → La UI se actualiza automáticamente
3. **Room guarda datos** → Persistencia local
4. **Hilt inyecta dependencias** → Menos acoplamiento
5. **Compose es declarativo** → Código más simple

### 🎓 Recursos de Aprendizaje

- [Android Developers - Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Developers - ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Android Developers - Room](https://developer.android.com/training/data-storage/room)
- [Android Developers - Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 📝 Requisitos del Proyecto (ABPRO)

Este proyecto cumple 100% con los requisitos de la actividad:

- ✅ **Temporizador que persiste** en rotación y cambio de apps
- ✅ **Control completo del ciclo de vida** con logs
- ✅ **ViewModel obligatoria** para persistencia
- ✅ **CountDownTimer gestionado** correctamente
- ✅ **Logs de ciclo de vida** en Logcat
- ✅ **Persistencia de estado** con ViewModel

### 🚀 Mejoras Adicionales

- Sistema profesional de ejercicios
- Historial real con Room Database
- UI moderna con Jetpack Compose
- Múltiples modos de temporizador
- Sistema anti-duplicados
- Arquitectura limpia y escalable

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Tests unitarios
./gradlew test

# Tests de instrumentación
./gradlew connectedAndroidTest
```

### Cobertura

- ✅ ViewModels
- ✅ Repositorios
- ✅ Use Cases (futuro)

---

## 📸 Capturas de Pantalla

*(Incluir capturas de las pantallas principales)*

### Pantallas Principales

1. **Selección de Ejercicios** - Catálogo con búsqueda y filtros
2. **Temporizador** - Cronómetro circular animado
3. **Historial** - Estadísticas y sesiones por día
4. **Rutinas** - Rutinas predefinidas

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Para cambios importantes:

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto es parte de una actividad académica.

---

## 👨‍💻 Desarrollador

<div align="center">

**Giorgio Interdonato Palacios**

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/DonGeeo87)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/giorgio-interdonato)

</div>

---

<div align="center">

**TimerFit Pro** - El temporizador definitivo para entrenamientos profesionales 🏋️‍♂️

⭐ Si te gusta el proyecto, dale una estrella en GitHub ⭐

</div>
