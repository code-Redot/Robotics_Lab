package com.example.robotics_lab


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.Fragment
import com.example.robotics_lab.databinding.FragmentProductBinding
import com.bumptech.glide.Glide


////////////////////////////////////////////////////////////////////////////////////////////////////
class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getString("productId")
        val productName = arguments?.getString("productName")
        val productManufacturer = arguments?.getString("productManufacturer")
        val productStock = arguments?.getString("productStock")
        val productMSRP = arguments?.getString("productMSRP")
        val productDescription = arguments?.getString("productDescription")
        val productUsefulUrls = arguments?.getString("productUsefulUrls")
        val productImage = arguments?.getString("productImage")

        Log.d(TAG, "Product ID: $productId")
        Log.d(TAG, "Product Name: $productName")
        Log.d(TAG, "Product Manufacturer: $productManufacturer")
        Log.d(TAG, "Product Stock: $productStock")
        Log.d(TAG, "Product MSRP: $productMSRP")
        Log.d(TAG, "Product Description: $productDescription")
        Log.d(TAG, "Product Useful URLs: $productUsefulUrls")
        Log.d(TAG, "Product Image: $productImage")

        binding.textViewProductName.text = productName
        binding.textViewProductManufacturer.text = productManufacturer
        binding.textViewProductStock.text = getString(R.string.product_stock, productStock)
        binding.textViewProductMSRP.text = getString(R.string.product_msrp, productMSRP)
        binding.textViewProductDescription.text = productDescription
        binding.textViewProductUsefulUrls.text = productUsefulUrls

        // Load and display the product image using Glide
        if (productImage != null && productImage.isNotEmpty()) {
            Glide.with(requireContext())
                .load(productImage)
                .placeholder(R.drawable.ic_product_image) // Placeholder while loading
                .error(R.drawable.ic_error_image) // Error drawable on failure
                .into(binding.imageViewProduct)
        }

    }

    companion object {
        const val TAG = "ProductFragment"
    }
}

