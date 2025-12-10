package dev.dongeeo.timerfitpro.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Mapeo de nombres de Ionicons a iconos de Material Icons como alternativa
 * 
 * Para usar Ionicons reales:
 * 1. Descarga los SVGs desde https://ionic.io/ionicons
 * 2. Conviértelos a Vector Drawables usando Android Studio
 * 3. Reemplaza estos mapeos con los Vector Drawables reales
 */
object Ionicons {
    // Fitness & Gym icons (usando Material Icons básicos como placeholder)
    val Fitness = Icons.Default.Star
    val Barbell = Icons.Default.Star
    val Dumbbell = Icons.Default.Star
    val Body = Icons.Default.Person
    
    // Cardio icons
    val Walk = Icons.Default.Star // Placeholder - usar Vector Drawable de Ionicons
    val Bicycle = Icons.Default.Star // Placeholder
    val Pulse = Icons.Default.Favorite
    
    // Core & Flexibility
    val Heart = Icons.Default.Favorite
    val Leaf = Icons.Default.Favorite // Placeholder
    val Flower = Icons.Default.Favorite // Placeholder
    
    // General
    val Play = Icons.Default.PlayArrow
    val Pause = Icons.Default.PlayArrow // Placeholder - usar Vector Drawable
    val Stop = Icons.Default.Close // Placeholder
    val Checkmark = Icons.Default.Check
    val Close = Icons.Default.Close
}

/**
 * Helper para obtener iconos de Ionicons por nombre
 * 
 * Uso:
 * val icon = IoniconsHelper.getIcon("fitness")
 * val icon = IoniconsHelper.getIcon("barbell")
 */
object IoniconsHelper {
    fun getIcon(name: String): ImageVector {
        return when (name.lowercase()) {
            "fitness", "fitness-outline", "fitness-sharp" -> Ionicons.Fitness
            "barbell", "barbell-outline", "barbell-sharp" -> Ionicons.Barbell
            "dumbbell", "dumbbell-outline", "dumbbell-sharp" -> Ionicons.Dumbbell
            "body", "body-outline", "body-sharp" -> Ionicons.Body
            "walk", "walk-outline", "walk-sharp" -> Ionicons.Walk
            "bicycle", "bicycle-outline", "bicycle-sharp" -> Ionicons.Bicycle
            "pulse", "pulse-outline", "pulse-sharp" -> Ionicons.Pulse
            "heart", "heart-outline", "heart-sharp" -> Ionicons.Heart
            "leaf", "leaf-outline", "leaf-sharp" -> Ionicons.Leaf
            "flower", "flower-outline", "flower-sharp" -> Ionicons.Flower
            "play", "play-outline", "play-sharp" -> Ionicons.Play
            "pause", "pause-outline", "pause-sharp" -> Ionicons.Pause
            "stop", "stop-outline", "stop-sharp" -> Ionicons.Stop
            "checkmark", "checkmark-outline", "checkmark-sharp" -> Ionicons.Checkmark
            "close", "close-outline", "close-sharp" -> Ionicons.Close
            else -> Icons.Default.Star // Default fallback
        }
    }
}

