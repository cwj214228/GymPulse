package com.gympulse.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ForgeDarkColorScheme = darkColorScheme(
    primary = ForgeAccent,
    onPrimary = ForgeOnAccent,
    secondary = ForgeAccent2,
    background = ForgeBlack,
    surface = ForgeSurface,
    surfaceVariant = ForgeSurface2,
    onBackground = ForgeFg,
    onSurface = ForgeFg,
    onSurfaceVariant = ForgeMuted,
    outline = ForgeBorder,
    outlineVariant = ForgeBorder,
    error = ForgeDanger,
)

@Composable
fun GymPulseTheme(content: @Composable () -> Unit) {
    val colorScheme = ForgeDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ForgeBlack.toArgb()
            window.navigationBarColor = ForgeBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GymPulseTypography,
        content = content
    )
}
