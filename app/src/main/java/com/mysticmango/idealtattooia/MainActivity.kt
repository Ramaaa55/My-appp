package com.mysticmango.idealtattooia

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private val styles = listOf(
        Style("Japones", R.drawable.estilo_japones),
        Style("Realista", R.drawable.estilo_realista),
        Style("Tradicional", R.drawable.estilo_tradicional),
        Style("Sagrado", R.drawable.estilo_sagrado),
        Style("Futurista", R.drawable.estilo_futurista)
    )

    private var selectedStyle: Style? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupStyles()
        setupButtons()
    }

    private fun setupStyles() {
        val container = findViewById<LinearLayout>(R.id.stylesContainer)
        val inflater = LayoutInflater.from(this)

        styles.forEach { style ->
            val view = inflater.inflate(R.layout.item_style, container, false) as MaterialCardView

            view.findViewById<ImageView>(R.id.styleImage).setImageResource(style.imageRes)
            view.findViewById<TextView>(R.id.styleName).text = style.name

            view.setOnClickListener {
                selectedStyle = style
                updateStyleSelection(container, view)
            }

            container.addView(view)
        }
    }

    private fun updateStyleSelection(container: LinearLayout, selectedView: MaterialCardView) {
        // Reset all cards
        for (i in 0 until container.childCount) {
            val card = container.getChildAt(i) as MaterialCardView
            card.strokeWidth = 0
        }

        // Highlight selected card
        selectedView.strokeWidth = resources.getDimensionPixelSize(R.dimen.spacing_small)
        selectedView.strokeColor = getColor(R.color.orange_primary)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.surpriseButton).setOnClickListener {
            // TODO: Implement surprise functionality
        }

        findViewById<Button>(R.id.generateButton).setOnClickListener {
            // TODO: Implement generate functionality
        }
    }
}

data class Style(
    val name: String,
    val imageRes: Int
)

