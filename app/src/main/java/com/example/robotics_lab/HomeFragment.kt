package com.example.robotics_lab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.robotics_lab.databinding.FragmentHomeBinding
import com.google.firebase.database.*


////////////////////////////////////////////////////////////////////////////////////////////////////
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var productAdapter: ProductAdapter
    private val productDetailsList: MutableList<ProductDetails> = mutableListOf()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("products")

        // Set up RecyclerView and adapter
        binding.recyclerViewProductList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            productAdapter = ProductAdapter(productDetailsList) { product ->
                navigateToProduct(product)
            }
            adapter = productAdapter
        }

        // Load products from the database
        loadProducts()
    }

    private fun navigateToProduct(productDetails: ProductDetails) {
        val bundle = Bundle()
        bundle.putString("productId", productDetails.productId)
        bundle.putString("productName", productDetails.productName)
        bundle.putString("productManufacturer", productDetails.productManufacturer)
        bundle.putString("productStock", productDetails.productStock.toString())
        bundle.putString("productMSRP", productDetails.productMSRP.toString())
        bundle.putString("productDescription", productDetails.productDescription)
        bundle.putString("productUsefulUrls", productDetails.productUsefulUrls)
        bundle.putString("productImage", productDetails.productImageUrl)

        val productFragment = ProductFragment()
        productFragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_content, productFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun loadProducts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productDetailsList.clear()
                for (productSnapshot in snapshot.children) {
                    val productDetails = productSnapshot.getValue(ProductDetails::class.java)
                    productDetails?.let {
                        productDetailsList.add(it)
                    }
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }
    private fun navigateToProduct(productId: String, productName: String, productManufacturer: String, productStock: String, productMSRP: String, productDescription: String, productUsefulUrls: String, productImage: String) {
        val bundle = Bundle().apply {
            putString("productId", productId)
            putString("productName", productName)
            putString("productManufacturer", productManufacturer)
            putString("productStock", productStock)
            putString("productMSRP", productMSRP)
            putString("productDescription", productDescription)
            putString("productUsefulUrls", productUsefulUrls)
            putString("productImage", productImage)
        }

        val productFragment = ProductFragment()
        productFragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_content, productFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
