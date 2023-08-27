package com.example.robotics_lab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EquipmentAdapter(
    private val context: Context,
    private val equipmentList: List<Equipment>,
    private val onItemClick: (Equipment) -> Unit // Add onItemClick parameter
) : RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipment = equipmentList[position]

        // Bind equipment data to the ViewHolder
        holder.bind(equipment)

        // Set click listener to handle item click
        holder.itemView.setOnClickListener { onItemClick(equipment) }
    }

    override fun getItemCount(): Int {
        return equipmentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val equipmentImageView: ImageView = itemView.findViewById(R.id.imageViewEquipment)
        private val equipmentNameTextView: TextView = itemView.findViewById(R.id.equipmentNameTextView)
        private val modelNumberTextView: TextView = itemView.findViewById(R.id.modelNumberTextView)

        fun bind(equipment: Equipment) {
            // Load equipment image using Glide or your preferred image loading library
            Glide.with(context)
                .load(equipment.photos?.firstOrNull()) // Load the first equipment image
                .placeholder(R.drawable.ic_product_image) // Placeholder image
                .into(equipmentImageView)

            // Set the equipment name and model number
            equipmentNameTextView.text = equipment.equipmentName
            modelNumberTextView.text = equipment.modelNumber
        }
    }
}
