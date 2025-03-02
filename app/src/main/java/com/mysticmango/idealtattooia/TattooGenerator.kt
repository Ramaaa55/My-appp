package com.mysticmango.idealtattooia

import com.mysticmango.idealtattooia.R
import com.mysticmango.idealtattooia.TattooStyle
import com.mysticmango.idealtattooia.TattooStyleProvider
import kotlin.random.Random

object TattooGenerator {
    fun generateRandomDesign(): TattooStyle {
        val styles = TattooStyleProvider.defaultStyles()
        return styles[Random.nextInt(styles.size)]
    }
}