package com.mysticmango.idealtattooia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mysticmango.idealtattooia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedStyle: Style? = null

    private val styles = listOf(
        Style("Japones", R.drawable.estilo_japones),
        Style("Realista", R.drawable.estilo_realista),
        Style("Tradicional", R.drawable.estilo_tradicional),
        Style("Sagrado", R.drawable.estilo_sagrado),
        Style("Futurista", R.drawable.estilo_futurista)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStylesRecycler()
        setupButtons()
    }

    private fun setupStylesRecycler() {
        binding.stylesRecycler.adapter = StylesAdapter(styles) { style ->
            selectedStyle = style
        }

        // Add padding to show partial next item
        binding.stylesRecycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: android.view.View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position == state.itemCount - 1) {
                    outRect.right = resources.getDimensionPixelSize(R.dimen.spacing_xl)
                }
            }
        })
    }

    private fun setupButtons() {
        binding.surpriseButton.setOnClickListener {
            // TODO: Implement surprise functionality
        }

        binding.generateButton.setOnClickListener {
            // TODO: Implement generate functionality
        }
    }
}

data class Style(
    val name: String,
    val imageRes: Int
)

