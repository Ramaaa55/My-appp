package com.mysticmango.idealtattooia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mysticmango.idealtattooia.TattooStyle
import com.mysticmango.idealtattooia.databinding.ItemStyleCardBinding

class TattooAdapter(
    private val styles: List<TattooStyle>,
    private val onStyleSelected: (TattooStyle) -> Unit
) : RecyclerView.Adapter<TattooAdapter.StyleViewHolder>() {

    private var selectedPosition = -1

    inner class StyleViewHolder(
        private val binding: ItemStyleCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(style: TattooStyle, isSelected: Boolean) {
            with(binding) {
                styleImage.setImageResource(style.imageRes)
                styleName.text = style.name
                root.isSelected = isSelected

                root.setOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        val previousSelected = selectedPosition
                        selectedPosition = bindingAdapterPosition
                        notifyItemChanged(selectedPosition)
                        onStyleSelected(style)
                    }
                }
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
    }

    override fun getItemCount(): Int = styles.size

    fun getSelectedStyle(): TattooStyle? {
        return if (selectedPosition != -1) styles[selectedPosition] else null
    }
} 