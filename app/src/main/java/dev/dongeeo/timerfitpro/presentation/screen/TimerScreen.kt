package dev.dongeeo.timerfitpro.presentation.screen

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.dongeeo.timerfitpro.domain.model.TimerProgressColor
import dev.dongeeo.timerfitpro.presentation.theme.TimerGreen
import dev.dongeeo.timerfitpro.presentation.theme.TimerRed
import dev.dongeeo.timerfitpro.presentation.theme.TimerYellow
import dev.dongeeo.timerfitpro.presentation.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onBack: () -> Unit,
    onHistory: () -> Unit,
    viewModel: TimerViewModel
) {
    val timerState by viewModel.timerState.collectAsState()
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
    
    // Vibrar en los últimos 5 segundos
    LaunchedEffect(timerState.timeLeftMillis) {
        if (timerState.timeLeftMillis in 5000..5100 && timerState.isRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                (vibrator as? Vibrator)?.vibrate(200)
            }
        }
    }
    
    // Guardar sesión cuando termine (solo una vez por sesión)
    var lastSavedState by remember { 
        mutableStateOf<Triple<Long, Boolean, Long>?>(null) 
    }
    
    LaunchedEffect(timerState.isRunning, timerState.timeLeftMillis, timerState.totalTimeMillis) {
        val currentState = Triple(
            timerState.timeLeftMillis,
            timerState.isRunning,
            timerState.totalTimeMillis
        )
        
        // Solo guardar si el timer terminó y no hemos guardado este estado específico
        if (!timerState.isRunning && 
            timerState.timeLeftMillis == 0L && 
            timerState.totalTimeMillis > 0 &&
            lastSavedState != currentState) {
            lastSavedState = currentState
            viewModel.saveSession()
        }
        
        // Resetear cuando el timer se reinicia (nuevo totalTimeMillis)
        if (timerState.isRunning && timerState.timeLeftMillis > 0) {
            lastSavedState = null
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TimerFit Pro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                },
                actions = {
                    TextButton(onClick = onHistory) {
                        Text("Historial")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Cronómetro circular
            CircularTimer(
                progress = timerState.progress,
                progressColor = timerState.progressColor,
                timeLeftMillis = timerState.timeLeftMillis,
                modifier = Modifier.size(300.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Información del ejercicio y modo
            if (viewModel.selectedExercise != null) {
                Text(
                    text = viewModel.selectedExercise!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (timerState.mode == dev.dongeeo.timerfitpro.domain.model.TimerMode.HIIT) {
                Text(
                    text = "Ronda ${timerState.currentRound}/${timerState.totalRounds}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (timerState.isWorkPhase) "TRABAJO" else "DESCANSO",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (timerState.isWorkPhase) TimerRed else TimerGreen
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Botones de control con animaciones
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Pause/Resume/Start
                AnimatedVisibility(
                    visible = timerState.isRunning,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    if (timerState.isRunning) {
                        Button(
                            onClick = { viewModel.pauseTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TimerYellow
                            ),
                            modifier = Modifier
                                .size(72.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                },
                            shape = androidx.compose.foundation.shape.CircleShape
                        ) {
                            Text("⏸", fontSize = 28.sp)
                        }
                    }
                }
                
                AnimatedVisibility(
                    visible = timerState.isPaused && timerState.timeLeftMillis > 0,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    if (timerState.isPaused && timerState.timeLeftMillis > 0) {
                        Button(
                            onClick = { viewModel.resumeTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TimerGreen
                            ),
                            modifier = Modifier
                                .size(72.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                },
                            shape = androidx.compose.foundation.shape.CircleShape
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Reanudar",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                AnimatedVisibility(
                    visible = !timerState.isRunning && timerState.timeLeftMillis == 0L,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    if (!timerState.isRunning && timerState.timeLeftMillis == 0L) {
                        Button(
                            onClick = { viewModel.startFixedTime(60000) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TimerGreen
                            ),
                            modifier = Modifier
                                .size(72.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                },
                            shape = androidx.compose.foundation.shape.CircleShape
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Iniciar",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                // Botón Stop
                AnimatedVisibility(
                    visible = timerState.timeLeftMillis > 0 || timerState.isRunning,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    if (timerState.timeLeftMillis > 0 || timerState.isRunning) {
                        Button(
                            onClick = { viewModel.stopTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TimerRed
                            ),
                            modifier = Modifier
                                .size(72.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                },
                            shape = androidx.compose.foundation.shape.CircleShape
                        ) {
                            Text("⏹", fontSize = 28.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botones de modo rápido con mejor diseño
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = false,
                    onClick = { viewModel.startFixedTime(15000) },
                    label = { 
                        Text(
                            "15s",
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    }
                )
                FilterChip(
                    selected = false,
                    onClick = { viewModel.startFixedTime(30000) },
                    label = { 
                        Text(
                            "30s",
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    }
                )
                FilterChip(
                    selected = false,
                    onClick = { viewModel.startFixedTime(45000) },
                    label = { 
                        Text(
                            "45s",
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    }
                )
                FilterChip(
                    selected = false,
                    onClick = { viewModel.startFixedTime(60000) },
                    label = { 
                        Text(
                            "60s",
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón HIIT con mejor diseño
            Button(
                onClick = {
                    viewModel.startHIIT(
                        workMillis = 20000,
                        restMillis = 10000,
                        rounds = 8
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "🔥 HIIT (20s/10s x8)",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CircularTimer(
    progress: Float,
    progressColor: TimerProgressColor,
    timeLeftMillis: Long,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "timer_progress"
    )
    
    // Gradiente basado en el progreso
    val gradientBrush = remember(progressColor) {
        when (progressColor) {
            TimerProgressColor.GREEN -> Brush.sweepGradient(
                colors = listOf(
                    TimerGreen,
                    TimerGreen.copy(alpha = 0.8f)
                )
            )
            TimerProgressColor.YELLOW -> Brush.sweepGradient(
                colors = listOf(
                    TimerGreen,
                    TimerYellow,
                    TimerYellow.copy(alpha = 0.8f)
                )
            )
            TimerProgressColor.RED -> Brush.sweepGradient(
                colors = listOf(
                    TimerGreen,
                    TimerYellow,
                    TimerRed,
                    TimerRed.copy(alpha = 0.9f)
                )
            )
        }
    }
    
    // Efecto pulse en los últimos 3 segundos
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulse by if (timeLeftMillis <= 3000 && timeLeftMillis > 0) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(300),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        remember { mutableStateOf(1f) }
    }
    
    // Color del texto que cambia con el progreso
    val textColor by animateColorAsState(
        targetValue = when (progressColor) {
            TimerProgressColor.GREEN -> TimerGreen
            TimerProgressColor.YELLOW -> TimerYellow
            TimerProgressColor.RED -> TimerRed
        },
        animationSpec = tween(500),
        label = "text_color"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            // Fondo del círculo con gradiente sutil
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2C2C2C),
                        Color(0xFF1A1A1A)
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Círculo de progreso con gradiente
            val sweepAngle = 360f * animatedProgress
            drawArc(
                brush = gradientBrush,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            // Sombra interna para profundidad
            if (animatedProgress > 0.1f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle * 0.3f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth * 0.5f, cap = StrokeCap.Round),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }
        
        // Tiempo restante con color dinámico
        val minutes = (timeLeftMillis / 1000) / 60
        val seconds = (timeLeftMillis / 1000) % 60
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            fontSize = (48.sp * pulse),
            fontWeight = FontWeight.Bold,
            color = textColor,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
    }
}

