package com.reborn.wasteless.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reborn.wasteless.R
import com.reborn.wasteless.data.model.FoodLogSummary

class FoodLogAdapter(private val logs: List<FoodLogSummary>, private val mode: String = "HOME") :
    RecyclerView.Adapter<FoodLogAdapter.FoodLogViewHolder>() {
    inner class FoodLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.imgFood)
        val tvName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvWeight: TextView = itemView.findViewById(R.id.tvWeight)
        val tvWasteType: TextView = itemView.findViewById(R.id.tvWasteType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_card, parent, false)
        return FoodLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodLogViewHolder, position: Int) {
        val log = logs[position]
        holder.tvName.text = log.title
        holder.tvDate.text = log.date
        holder.tvWeight.text = log.totalWeight
        holder.tvWasteType.text = log.wasteType

        // Load log image when available; otherwise leave default drawable
        if (!log.imageUrl.isNullOrBlank()) {
            Glide.with(holder.itemView)
                .load(log.imageUrl)
                .centerCrop()
                .error(R.drawable.icon_sample_photo)
                .placeholder(R.drawable.icon_sample_photo)
                .into(holder.ivPhoto)
        } else {
            holder.ivPhoto.setImageResource(R.drawable.icon_sample_photo)
        }

        //Converting size layout
        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = if (mode == "DIARY") {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            dpToPx(holder.itemView.context, 200)
        }
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount() = logs.size

    private fun dpToPx(context: Context, dp: Int): Int =
        (dp * context.resources.displayMetrics.density).toInt()
}