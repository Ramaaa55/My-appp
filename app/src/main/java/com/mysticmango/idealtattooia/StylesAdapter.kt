package com.mysticmango.idealtattooia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mysticmango.idealtattooia.databinding.ItemStyleBinding

class StylesAdapter(
    private val styles: List<Style>,
    private val onStyleSelected: (Style) -> Unit
) : RecyclerView.Adapter<StylesAdapter.StyleViewHolder>() {

    private var selectedPosition = -1

    inner class StyleViewHolder(
        private val binding: ItemStyleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(style: Style, isSelected: Boolean) {
            binding.apply {
                styleImage.setImageResource(style.imageRes)
                styleName.text = style.name
                root.strokeWidth = if (isSelected) {
                    root.context.resources.getDimensionPixelSize(R.dimen.spacing_xs)
                } else 0
                root.strokeColor = if (isSelected) {
                    root.context.getColor(R.color.accent)
                } else 0

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onStyleSelected(style)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
        return StyleViewHolder(
            ItemStyleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
        holder.bind(styles[position], position == selectedPosition)
    }

    override fun getItemCount() = styles.size
}

