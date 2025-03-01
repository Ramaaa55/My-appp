package com.mysticmango.idealtattooia

data class TattooStyle(
    val imageRes: Int,
    val name: String
)

object TattooStyleProvider {
    fun defaultStyles(): List<TattooStyle> = listOf(
        TattooStyle(R.drawable.estilo_japones, "Japonés"),
        TattooStyle(R.drawable.estilo_realista, "Realista"),
        TattooStyle(R.drawable.estilo_tradicional, "Tradicional"),
        TattooStyle(R.drawable.estilo_sagrado, "Sagrado"),
        TattooStyle(R.drawable.estilo_futurista, "Futurista")
    )
}