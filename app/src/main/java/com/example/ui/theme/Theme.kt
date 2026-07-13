package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = OrangePrimaryDark,
    secondary = OrangeSecondaryDark,
    background = DarkGrayBackground,
    surface = DarkGraySurface,
    onPrimary = DarkGrayBackground,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = OrangePrimaryLight,
    secondary = OrangeSecondaryLight,
    background = LightGrayBackground,
    surface = LightGraySurface,
    onPrimary = Color.White,
    onBackground = DarkGrayBackground,
    onSurface = DarkGrayBackground
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color so our climbers experience the custom themed brand
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
