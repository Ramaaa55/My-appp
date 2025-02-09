package com.mysticmango.idealtattooia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.mysticmango.idealtattooia.databinding.ActivityMainBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var styleRecyclerView: RecyclerView
    private lateinit var promptEditText: TextInputEditText
    private lateinit var btnSuggest: MaterialButton
    private lateinit var btnGenerate: MaterialButton
    private val prompts = listOf(
        "Dragón tribal con flores de cerezo",
        "Geometría sagrada dorada",
        "Retrato de guerrero samurái",
        "Nave espacial cyberpunk",
        "Mandalas cósmicos con planetas"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views using view binding
        styleRecyclerView = binding.styleRecycler
        promptEditText = binding.promptInput
        btnSuggest = binding.surpriseButton
        btnGenerate = binding.generateButton

        // Configurar RecyclerView
        configurarEstilos()

        btnSuggest.setOnClickListener {
            val randomPrompt = prompts.random()
            promptEditText.setText(randomPrompt.toString())
            Toast.makeText(this, "Sugerencia: $randomPrompt", Toast.LENGTH_SHORT).show()
        }

        btnGenerate.setOnClickListener {
            val prompt = promptEditText.text.toString()
            if (prompt.isNotEmpty()) {
                generateTattooDesign(prompt, "SelectedStyle")
            } else {
                Toast.makeText(this, "Escribe un prompt primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarEstilos() {
        val styleRecycler = findViewById<RecyclerView>(R.id.styleRecycler)
        styleRecycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,  // Scroll horizontal
            false
        )

        // Espaciado entre items
        styleRecycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.HORIZONTAL
            ).apply {
                setDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.spacer_16dp)!!)
            }
        )

        val styles = TattooStyleProvider.defaultStyles()
        styleRecycler.adapter = StyleAdapter(styles) { selectedStyle ->
            Toast.makeText(
                this,
                "Estilo seleccionado: ${selectedStyle.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun generateTattooDesign(prompt: String, style: String) {
        // Lógica simulada de generación
        Toast.makeText(this, "Generando diseño: $prompt", Toast.LENGTH_LONG).show()

        // Aquí iría la integración real con la API
    }
}

