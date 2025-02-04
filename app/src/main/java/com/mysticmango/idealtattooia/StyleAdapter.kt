package com.mysticmango.idealtattooia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StyleAdapter(
    private val styles: MutableList<TattooStyle>,
    private val onStyleSelected: (TattooStyle) -> Unit
) : RecyclerView.Adapter<StyleAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val style = styles[position]
        Glide.with(holder.itemView)
            .load(style.imageResId)
            .into(holder.image)
        holder.bind(style)

        holder.itemView.setOnClickListener {
            styles.forEach { it.isSelected = false }
            style.isSelected = true
            notifyDataSetChanged()
            onStyleSelected(style)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.styleImage)

        fun bind(style: TattooStyle) {
            itemView.apply {
                findViewById<TextView>(R.id.styleText).text = style.name
                background = ContextCompat.getDrawable(
                    context,
                    if (style.isSelected) R.drawable.style_selected_bg
                    else R.drawable.style_default_bg
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_style, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = styles.size
} 