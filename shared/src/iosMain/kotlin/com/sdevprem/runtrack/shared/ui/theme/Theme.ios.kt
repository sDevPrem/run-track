package com.sdevprem.runtrack.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun AppTheme(
    useDarkTheme: Boolean,
    useDynamicColor: Boolean,
    content: @Composable (() -> Unit)
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}