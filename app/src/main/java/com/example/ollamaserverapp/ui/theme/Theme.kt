package com.example.ollamaserverapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),
    secondary = Color(0xFF1976D2),
    tertiary = Color(0xFFD32F2F)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val ElegantIvoryBlueLight = lightColorScheme(
        primary = Color(0xFF5B7FA8),
        secondary = Color(0xFFA08FA4),
        tertiary = Color(0xFFB38C6A),
        surfaceVariant = Color(0xFFF3EEE8),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onTertiary = Color.Black,
        primaryContainer = Color(0xFF8E7D6B)
    )

private val ElegantIvoryBlueDark = darkColorScheme(
        primary = Color(0xFF5B7FA8),
        secondary = Color(0xFFA08FA4),
        tertiary = Color(0xFFB38C6A),
        surfaceVariant = Color(0xFF2E2B28),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.Black,
        primaryContainer = Color(0xFF8E7D6B)
    )

private val LuxGreenGoldLight = lightColorScheme(
    primary = Color(0xFF51734C),
    secondary = Color(0xFF607D3B),
    tertiary = Color(0xFFD4AF37),
    surfaceVariant = Color(0xFFF0EFEA),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    primaryContainer = Color(0xFF8C7C62)
)

private val LuxGreenGoldDark = darkColorScheme(
    primary = Color(0xFF6A8B65),
    secondary = Color(0xFF7B9552),
    tertiary = Color(0xFFE1C15A),
    surfaceVariant = Color(0xFF2B2A26),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    primaryContainer = Color(0xFF74664E)
)

enum class AppPalette {
    Default,            //
    ElegantIvoryBlue,   // new theme A
    LuxGreenGold        // theme B
}

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun OllamaServerAppTheme(
    palette: AppPalette = AppPalette.ElegantIvoryBlue, //default theme
    dynamicColor: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (palette) {

        AppPalette.Default -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val ctx = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
            } else {
                if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }

        AppPalette.ElegantIvoryBlue -> {
            if (darkTheme) ElegantIvoryBlueDark else ElegantIvoryBlueLight
        }

        AppPalette.LuxGreenGold -> {
            if (darkTheme) LuxGreenGoldDark else LuxGreenGoldLight
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

