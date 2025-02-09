package com.mysticmango.idealtattooia

import android.content.res.Resources
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mysticmango.idealtattooia.databinding.ItemStyleCardBinding

class StyleAdapter(
    private val styles: List<TattooStyle>,
    private val onStyleSelected: (TattooStyle) -> Unit
) : RecyclerView.Adapter<StyleAdapter.StyleViewHolder>() {

    private var selectedPosition = -1

    inner class StyleViewHolder(private val binding: ItemStyleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(style: TattooStyle, isSelected: Boolean) {
            with(binding) {
                // Carga de imagen optimizada
                Glide.with(root.context)
                    .load(style.imageRes)
                    .override(250, 250)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_error_outline)
                    .into(styleImage)

                styleName.apply {
                    text = style.name.uppercase()
                    typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                    setLineSpacing(1.1f, 1.1f)

                    // Ajuste dinámico de tamaño
                    post {
                        val availableWidth = width - paddingStart - paddingEnd
                        paint.textSize = textSize
                        val textWidth = paint.measureText(text.toString())

                        textSize = if (textWidth > availableWidth) {
                            12f // Tamaño mínimo
                        } else {
                            16f // Tamaño máximo
                        }.coerceAtLeast(12f)
                    }
                }

                // Efecto de selección mejorado
                styleName.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (isSelected) R.color.accent_orange else R.color.white
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
        val binding = ItemStyleCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StyleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
        holder.bind(styles[position], position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = position
            notifyItemChanged(previous)
            notifyItemChanged(position)
            onStyleSelected(styles[position])
        }
    }

    override fun getItemCount() = styles.size

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}