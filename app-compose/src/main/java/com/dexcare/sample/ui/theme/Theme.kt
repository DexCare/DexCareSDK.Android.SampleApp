package com.dexcare.sample.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }
val LocalAppColor = staticCompositionLocalOf { AppLightColor }

@Composable
fun DexCareSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> AppDarkColor
        else -> AppLightColor
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalColorScheme provides LightColorScheme,
            LocalAppColor provides colorScheme,
        ) {
            content()
        }
    }
}


@Composable
fun PreviewUi(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    ui: @Composable () -> Unit
) {
    DexCareSampleTheme(isDarkTheme) {
        Surface {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                ui()
            }
        }
    }
}
