package com.gympulse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gympulse.app.ui.common.BottomNavBar
import com.gympulse.app.ui.navigation.GymPulseNavGraph
import com.gympulse.app.ui.navigation.Routes
import com.gympulse.app.ui.theme.ForgeBlack
import com.gympulse.app.ui.theme.ForgeBorder
import com.gympulse.app.ui.theme.GymPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GymPulseTheme {
                val app = application as GymPulseApp
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME

                // 底部导航仅在 3 个主页面显示
                val showBottomNav = currentRoute in listOf(Routes.HOME, Routes.LOG, Routes.STATS)

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 导航图
                        GymPulseNavGraph(
                            navController = navController,
                            repository = app.repository,
                            prefs = app.preferenceManager,
                            dataManager = app.dataManager,
                            modifier = Modifier.weight(1f)
                        )

                        // 底部导航
                        if (showBottomNav) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    if (route != currentRoute) {
                                        navController.navigate(route) {
                                            popUpTo(Routes.HOME) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
