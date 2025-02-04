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
    private val styles: List<TattooStyle>,
    private val onStyleSelected: (Int) -> Unit
) : RecyclerView.Adapter<StyleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.styleImage)
        val text: TextView = view.findViewById(R.id.styleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_style, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val style = styles[position]
        Glide.with(holder.itemView)
            .load(style.imageResId)
            .into(holder.image)
        holder.text.text = style.name

        holder.itemView.setOnClickListener {
            onStyleSelected(position)
        }
    }

    override fun getItemCount() = styles.size
} 