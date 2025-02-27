package com.mysticmango.idealtattooia

// Único archivo que contiene la definición del modelo
data class TattooStyle(
    val imageRes: Int,  // Usar mismo nombre en todas partes
    val name: String
)

object TattooStyleProvider {
    fun defaultStyles(): List<TattooStyle> {
        return listOf(
            TattooStyle(R.drawable.estilo_japones, "JAPONÉS"),
            TattooStyle(R.drawable.estilo_realista, "REALISTA"),
            TattooStyle(R.drawable.estilo_tradicional, "TRADICIONAL"),
            TattooStyle(R.drawable.estilo_sagrado, "SAGRADO"),
            TattooStyle(R.drawable.estilo_futurista, "FUTURISTA")
        )
    }
}