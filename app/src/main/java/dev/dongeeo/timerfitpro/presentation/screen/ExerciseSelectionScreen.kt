package dev.dongeeo.timerfitpro.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.dongeeo.timerfitpro.domain.model.Exercise
import dev.dongeeo.timerfitpro.domain.model.ExerciseCategory
import dev.dongeeo.timerfitpro.domain.model.Difficulty
import dev.dongeeo.timerfitpro.presentation.viewmodel.ExerciseViewModel
import dev.dongeeo.timerfitpro.presentation.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionScreen(
    onExerciseSelected: (Exercise) -> Unit,
    exerciseViewModel: ExerciseViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel
) {
    val allExercises by exerciseViewModel.getAllExercises().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    
    // Filtrar ejercicios
    val filteredExercises = remember(allExercises, searchQuery, selectedCategory) {
        allExercises.filter { exercise ->
            val matchesSearch = searchQuery.isEmpty() || 
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                exercise.group.contains(searchQuery, ignoreCase = true) ||
                exercise.description.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategory == null || exercise.category == selectedCategory
            
            matchesSearch && matchesCategory
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Ejercicios",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de búsqueda
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // Filtros por categoría
            CategoryFilterChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = if (selectedCategory == category) null else category
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Contador de resultados
            Text(
                text = "${filteredExercises.size} ejercicio${if (filteredExercises.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grid de ejercicios
            if (filteredExercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "No se encontraron ejercicios",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Intenta con otros filtros",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = {
                                timerViewModel.selectExercise(exercise)
                                onExerciseSelected(exercise)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Buscar ejercicio...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun CategoryFilterChips(
    selectedCategory: ExerciseCategory?,
    onCategorySelected: (ExerciseCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        ExerciseCategory.MULTIARTICULAR,
        ExerciseCategory.ISOLATION,
        ExerciseCategory.CARDIO,
        ExerciseCategory.CORE,
        ExerciseCategory.FLEXIBILITY
    )
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = getCategoryName(category),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                },
                leadingIcon = {
                    Text(
                        text = getExerciseIconEmoji(category),
                        fontSize = 16.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getCategoryColor(category).copy(alpha = 0.2f),
                    selectedLabelColor = getCategoryColor(category)
                )
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit
) {
    val iconEmoji = getExerciseIconEmoji(exercise.category)
    val iconColor = getCategoryColor(exercise.category)
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .scale(scale),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header con icono y dificultad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        iconColor.copy(alpha = 0.2f),
                                        iconColor.copy(alpha = 0.05f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iconEmoji,
                            fontSize = 24.sp
                        )
                    }
                }
                
                // Badge de dificultad
                DifficultyBadge(difficulty = exercise.difficulty)
            }
            
            // Nombre del ejercicio
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            
            // Grupo muscular
            Text(
                text = exercise.group,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Equipamiento
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = exercise.equipment,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DifficultyBadge(difficulty: Difficulty) {
    val (text, color) = when (difficulty) {
        Difficulty.BEGINNER -> "Fácil" to Color(0xFF4CAF50)
        Difficulty.INTERMEDIATE -> "Medio" to Color(0xFFFF9800)
        Difficulty.ADVANCED -> "Avanzado" to Color(0xFFF44336)
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.height(24.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun getExerciseIconEmoji(category: ExerciseCategory): String {
    return when (category) {
        ExerciseCategory.MULTIARTICULAR -> "🏋️"
        ExerciseCategory.CARDIO -> "🏃"
        ExerciseCategory.CORE -> "💪"
        ExerciseCategory.FLEXIBILITY -> "🧘"
        ExerciseCategory.ISOLATION -> "⚡"
    }
}

fun getCategoryName(category: ExerciseCategory): String {
    return when (category) {
        ExerciseCategory.MULTIARTICULAR -> "Multi"
        ExerciseCategory.CARDIO -> "Cardio"
        ExerciseCategory.CORE -> "Core"
        ExerciseCategory.FLEXIBILITY -> "Flex"
        ExerciseCategory.ISOLATION -> "Aislam."
    }
}

@Composable
fun getCategoryColor(category: ExerciseCategory): Color {
    return when (category) {
        ExerciseCategory.MULTIARTICULAR -> Color(0xFF6200EE)
        ExerciseCategory.CARDIO -> Color(0xFF03DAC6)
        ExerciseCategory.CORE -> Color(0xFFFF6B6B)
        ExerciseCategory.FLEXIBILITY -> Color(0xFF4ECDC4)
        ExerciseCategory.ISOLATION -> Color(0xFFFFB84D)
    }
}
