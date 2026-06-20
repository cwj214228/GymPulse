package com.gympulse.app.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gympulse.app.data.DataManager
import com.gympulse.app.data.PreferenceManager
import com.gympulse.app.data.TrainingRepository
import com.gympulse.app.ui.home.HomeScreen
import com.gympulse.app.ui.home.HomeViewModel
import com.gympulse.app.ui.log.LogScreen
import com.gympulse.app.ui.log.LogViewModel
import com.gympulse.app.ui.settings.SettingsScreen
import com.gympulse.app.ui.stats.StatsScreen
import com.gympulse.app.ui.stats.StatsViewModel
import com.gympulse.app.ui.workout.WorkoutConfirmScreen

object Routes {
    const val HOME = "home"
    const val LOG = "log"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val WORKOUT_CONFIRM = "workout_confirm/{parts}"

    fun workoutConfirm(parts: String): String = "workout_confirm/$parts"
}

@Composable
fun GymPulseNavGraph(
    navController: NavHostController,
    repository: TrainingRepository,
    prefs: PreferenceManager,
    dataManager: DataManager,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        // ── 首页 ──
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository, prefs)
            )
            HomeScreen(
                onNavigateToLog = {
                    navController.navigate(Routes.LOG) { launchSingleTop = true }
                },
                onOpenSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onWorkoutSaved = {
                    val parts = viewModel.selectedPartsNames().joinToString(",")
                    navController.navigate(Routes.workoutConfirm(parts))
                }
            )
        }

        // ── 训练记录 ──
        composable(Routes.LOG) {
            val viewModel: LogViewModel = viewModel(
                factory = LogViewModel.Factory(repository)
            )
            LogScreen(viewModel = viewModel)
        }

        // ── 统计分析 ──
        composable(Routes.STATS) {
            val viewModel: StatsViewModel = viewModel(
                factory = StatsViewModel.Factory(repository)
            )
            StatsScreen(viewModel = viewModel)
        }

        // ── 设置 ──
        composable(Routes.SETTINGS) {
            SettingsScreen(
                dataManager = dataManager,
                onBack = { navController.popBackStack() }
            )
        }

        // ── 训练确认 ──
        composable(
            route = Routes.WORKOUT_CONFIRM,
            arguments = listOf(navArgument("parts") { type = NavType.StringType })
        ) { backStackEntry ->
            val partsArg = backStackEntry.arguments?.getString("parts") ?: ""
            val parts = if (partsArg.isBlank()) emptyList()
            else partsArg.split(",").filter { it.isNotBlank() }

            WorkoutConfirmScreen(
                savedParts = parts,
                onBackToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
