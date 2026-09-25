package com.exemplo.motortuner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LaranjaOficina = Color(0xFFE65100)
private val LaranjaClaro = Color(0xFFFF833A)
private val Aco = Color(0xFF37474F)
private val AcoClaro = Color(0xFF62757F)
private val FundoClaro = Color(0xFFF5F5F5)
private val FundoEscuro = Color(0xFF121212)

private val CoresClaro = lightColorScheme(
    primary = LaranjaOficina,
    onPrimary = Color.White,
    secondary = Aco,
    onSecondary = Color.White,
    background = FundoClaro,
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    error = Color(0xFFB00020)
)

private val CoresEscuro = darkColorScheme(
    primary = LaranjaClaro,
    onPrimary = Color.Black,
    secondary = AcoClaro,
    onSecondary = Color.Black,
    background = FundoEscuro,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = Color(0xFFCF6679)
)

@Composable
fun MotorTunerTheme(
    escuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (escuro) CoresEscuro else CoresClaro,
        content = content
    )
}
