package com.mysticmango.idealtattooia

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun IdealTattooIATheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}