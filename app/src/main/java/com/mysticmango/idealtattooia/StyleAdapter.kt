package com.mysticmango.idealtattooia

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.mysticmango.idealtattooia.databinding.ItemStyleCardBinding
import com.mysticmango.idealtattooia.TattooStyle

class StyleAdapter(
    private val styles: List<TattooStyle>,
    private val onStyleSelected: (Int) -> Unit
) : RecyclerView.Adapter<StyleAdapter.ViewHolder>() {

    private var selectedPosition = -1

    fun getSelectedStyle(): TattooStyle? {
        return if (selectedPosition != -1) styles[selectedPosition] else null
    }

    inner class ViewHolder(private val binding: ItemStyleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(style: TattooStyle, isSelected: Boolean) {
            with(binding) {
                val cardView = root as MaterialCardView

                // Configurar borde solo cuando está seleccionado
                cardView.strokeWidth = if (isSelected) 4.dpToPx() else 0
                cardView.strokeColor = Color.WHITE

                Glide.with(itemView)
                    .load(style.imageRes)
                    .override(200, 200)
                    .into(styleImage)

                styleName.text = style.name

                cardView.setOnClickListener {
                    val previous = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previous)
                    notifyItemChanged(selectedPosition)
                    onStyleSelected(adapterPosition)
                }
            }
        }

        private fun Int.dpToPx(): Int = (this * itemView.resources.displayMetrics.density).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStyleCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val style = styles[position]
        holder.bind(style, position == selectedPosition)
    }

    override fun getItemCount() = styles.size
}