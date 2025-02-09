package com.mysticmango.idealtattooia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val oldPosition = selectedPosition
                        selectedPosition = adapterPosition
                        notifyItemChanged(oldPosition)
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
}