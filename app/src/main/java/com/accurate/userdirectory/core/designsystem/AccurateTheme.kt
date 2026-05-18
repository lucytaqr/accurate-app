package com.accurate.userdirectory.core.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AccurateColors.PrimaryPink,
    onPrimary = AccurateColors.Surface,
    primaryContainer = AccurateColors.PrimaryPinkLight,
    secondary = AccurateColors.Info,
    onSecondary = AccurateColors.Surface,
    background = AccurateColors.Background,
    onBackground = AccurateColors.TextPrimary,
    surface = AccurateColors.Surface,
    onSurface = AccurateColors.TextPrimary,
    surfaceVariant = AccurateColors.SurfaceSoft,
    onSurfaceVariant = AccurateColors.TextSecondary,
    outline = AccurateColors.Border,
    error = AccurateColors.Error,
    onError = AccurateColors.Surface
)

@Composable
fun AccurateTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AccurateTypography,
        content = content
    )
}
