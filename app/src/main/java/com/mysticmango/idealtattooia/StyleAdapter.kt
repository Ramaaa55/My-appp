package com.mysticmango.idealtattooia

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.card.MaterialCardView
import android.content.res.ColorStateList

class StyleAdapter(
    private val styles: List<TattooStyle>,
    private val onStyleSelected: (Int) -> Unit
) : RecyclerView.Adapter<StyleAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.styleCard)
        val imageView: ImageView = itemView.findViewById(R.id.styleImage)
        val textView: TextView = itemView.findViewById(R.id.styleName)
        val selectionBadge: ImageView = itemView.findViewById(R.id.selectionBadge)

        fun bind(style: TattooStyle, isSelected: Boolean) {
            card.isSelected = isSelected
            textView.text = style.name.take(15).let {
                if (it.length < style.name.length) "$it..." else it
            }
            textView.visibility = View.VISIBLE
            val context = this.itemView.context
            Glide.with(context)
                .load(style.imageRes)
                .override(1600, 900)
                .fitCenter()
                .into(imageView)

            itemView.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onStyleSelected(adapterPosition)
            }

            updateSelectionState(isSelected)
        }

        private fun updateSelectionState(isSelected: Boolean) {
            val context = itemView.context
            card.apply {
                strokeWidth = context.resources.getDimensionPixelSize(R.dimen.stroke_width)
                strokeColor = ContextCompat.getColorStateList(context, R.color.accent_orange)
                setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background))
            }

            textView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.accent_orange else R.color.text_secondary_dark
                )
            )

            selectionBadge.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_style_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val style = styles[position]
        val context = holder.itemView.context

        holder.card.apply {
            strokeWidth = context.resources.getDimensionPixelSize(R.dimen.stroke_width)
            strokeColor = ContextCompat.getColorStateList(context, R.color.accent_orange)
            setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.card_background)
            )
        }

        holder.bind(style, position == selectedPosition)

        if (checkAspectRatio(context, style.imageRes)) {
            Log.w("StyleAdapter", "Imagen ${style.name} no cumple ratio 16:9")
        }
    }

    override fun getItemCount() = styles.size

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun checkAspectRatio(context: Context, @DrawableRes resId: Int): Boolean {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(context.resources, resId, options)
        return (options.outWidth.toFloat() / options.outHeight) != (16f/9)
    }
}