package dev.dongeeo.timerfitpro.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.dongeeo.timerfitpro.domain.model.Exercise
import dev.dongeeo.timerfitpro.presentation.screen.ExerciseSelectionScreen
import dev.dongeeo.timerfitpro.presentation.screen.HistoryScreen
import dev.dongeeo.timerfitpro.presentation.screen.RoutinesScreen
import dev.dongeeo.timerfitpro.presentation.screen.TimerScreen
import dev.dongeeo.timerfitpro.presentation.viewmodel.TimerViewModel

sealed class Screen(val route: String) {
    object ExerciseSelection : Screen("exercise_selection")
    object Timer : Screen("timer")
    object History : Screen("history")
    object Routines : Screen("routines")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    timerViewModel: TimerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ExerciseSelection.route
    ) {
        composable(Screen.ExerciseSelection.route) {
            ExerciseSelectionScreen(
                onExerciseSelected = { 
                    navController.navigate(Screen.Timer.route)
                },
                timerViewModel = timerViewModel
            )
        }
        
        composable(Screen.Timer.route) {
            TimerScreen(
                onBack = { navController.popBackStack() },
                onHistory = { navController.navigate(Screen.History.route) },
                viewModel = timerViewModel
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Routines.route) {
            RoutinesScreen(
                onBack = { navController.popBackStack() },
                onExerciseSelected = { exercise ->
                    timerViewModel.selectExercise(exercise)
                    navController.navigate(Screen.Timer.route)
                }
            )
        }
    }
}

