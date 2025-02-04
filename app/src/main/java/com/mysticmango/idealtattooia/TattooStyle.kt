package com.mysticmango.idealtattooia

// Single source of truth for TattooStyle
data class TattooStyle(
    val imageResId: Int,
    val name: String
)

object TattooStyleProvider {
    fun defaultStyles() = listOf(
        TattooStyle(R.drawable.estilo_futurista, "Futurista"),
        TattooStyle(R.drawable.estilo_japones, "Japonés"),
        TattooStyle(R.drawable.estilo_realista, "Realista"),
        TattooStyle(R.drawable.estilo_sagrado, "Sagrado"),
        TattooStyle(R.drawable.estilo_tradicional, "Tradicional")
    )
}