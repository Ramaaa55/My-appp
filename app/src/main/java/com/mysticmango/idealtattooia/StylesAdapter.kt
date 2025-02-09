package com.mysticmango.idealtattooia

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class StylesAdapter(
    private val styles: List<TattooStyle>,
    private val onStyleSelected: (Int) -> Unit
) : RecyclerView.Adapter<StylesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.styleImage)
        val name: TextView = itemView.findViewById(R.id.styleName)
        val selectionBadge: ImageView = itemView.findViewById(R.id.selectionBadge)
    }

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.style_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val style = styles[position]

        Glide.with(holder.itemView)
            .load(style.imageRes)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_error)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)

        holder.name.text = style.name
        holder.selectionBadge.visibility = if (position == selectedPosition) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = position
            notifyItemChanged(previous)
            notifyItemChanged(position)
            onStyleSelected(position)
        }

        // Add text shadow for better readability
        holder.name.setShadowLayer(4f, 0f, 2f, Color.BLACK)

        // Animate selection changes
        val scale = if (position == selectedPosition) 1.05f else 1f
        holder.itemView.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
    }

    override fun getItemCount() = styles.size
}

