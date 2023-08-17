package com.example.robotics_lab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

////////////////////////////////////////////////////////////////////////////////////////////////////
class ProductAdapter(
    private val productDetailsList: List<ProductDetails>,
    private val onItemClick: (productDetails: ProductDetails) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productNameTextView: TextView = view.findViewById(R.id.productNameTextView)
        val productImageView: ImageView = view.findViewById(R.id.productImageView)
        val productPriceTextView: TextView = view.findViewById(R.id.productPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productDetailsList[position]

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(product.productImageUrl)
            .placeholder(R.drawable.ic_product_image)
            .error(R.drawable.ic_error_image)
            .into(holder.productImageView)

        holder.productNameTextView.text = product.productName
        holder.productPriceTextView.text = product.productMSRP.toString()

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount(): Int {
        return productDetailsList.size
    }


}
