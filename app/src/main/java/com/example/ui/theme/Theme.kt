package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val BasokaDarkColorScheme = darkColorScheme(
    primary = BasokaPrimary,
    onPrimary = Color.White,
    primaryContainer = BasokaPrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = BasokaSecondary,
    onSecondary = Color.White,
    tertiary = BasokaTertiary,
    background = BasokaBlack,
    onBackground = BasokaTextPrimary,
    surface = BasokaSurface,
    onSurface = BasokaTextPrimary,
    surfaceVariant = BasokaSurfaceElevated,
    onSurfaceVariant = BasokaTextSecondary,
    outline = BasokaSurfaceBorder
)

@Composable
fun BasokaTheme(
    content: @Composable () -> Unit
) {
    // BASOKA enforces Kurdish Sorani RTL (Right to Left) for natural reading and high fidelity
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = BasokaDarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}

