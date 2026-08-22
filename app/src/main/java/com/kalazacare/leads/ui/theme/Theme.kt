package com.kalazacare.leads.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LeadsColorScheme = lightColorScheme(
    primary          = LeadsTeal,
    onPrimary        = White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = LeadsDarkTeal,

    secondary        = LeadsAmber,
    onSecondary      = White,
    secondaryContainer = Color(0xFFFFECD1),
    onSecondaryContainer = Color(0xFF7A4A00),

    error            = StatusError,
    onError          = White,
    errorContainer   = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background       = White,
    onBackground     = OnSurface,
    surface          = White,
    onSurface        = OnSurface,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,

    outline          = Outline,
    outlineVariant   = Color(0xFFEEEEEE),

    inverseSurface   = OnSurface,
    inverseOnSurface = White,
    inversePrimary   = Color(0xFF80CBC4),
)

@Composable
fun KalazaLeadsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LeadsColorScheme,
        typography  = LeadsTypography,
        shapes      = LeadsShapes,
        content     = content,
    )
}
