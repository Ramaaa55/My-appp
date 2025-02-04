package com.mysticmango.idealtattooia

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val styles = listOf(
        Style("Japones", R.drawable.estilo_japones),
        Style("Realista", R.drawable.estilo_realista),
        Style("Tradicional", R.drawable.estilo_tradicional),
        Style("Sagrado", R.drawable.estilo_sagrado),
        Style("Futurista", R.drawable.estilo_futurista)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupStylesRecyclerView()
        setupButtons()
    }

    private fun setupStylesRecyclerView() {
        val container = findViewById<LinearLayout>(R.id.stylesScrollView)
            .getChildAt(0) as LinearLayout

        styles.forEachIndexed { index, style ->
            val view = container.getChildAt(index)
            view.findViewById<ImageView>(R.id.styleImageView)
                .setImageResource(style.imageRes)
            view.findViewById<TextView>(R.id.styleNameTextView)
                .text = style.name
        }
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.generateButton).setOnClickListener {
            // TODO: Implement generate functionality
        }

        findViewById<MaterialButton>(R.id.surpriseButton).setOnClickListener {
            // TODO: Implement surprise functionality
        }
    }
}

data class Style(
    val name: String,
    val imageRes: Int
)

