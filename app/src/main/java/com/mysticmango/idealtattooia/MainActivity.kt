package com.mysticmango.idealtattooia

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.mysticmango.idealtattooia.R
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

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
        val styleRecycler = findViewById<RecyclerView>(R.id.styleRecycler).apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = StyleAdapter(TattooStyleProvider.defaultStyles()) { position ->
                Toast.makeText(
                    this@MainActivity,
                    "Selected: ${TattooStyleProvider.defaultStyles()[position].name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

