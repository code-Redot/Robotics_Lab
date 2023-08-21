package com.example.robotics_lab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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
        binding.recyclerViewProductList.layoutManager = GridLayoutManager(requireContext(), 2)
        productAdapter = ProductAdapter(productDetailsList) { product ->
            navigateToProduct(product)
        }
        binding.recyclerViewProductList.adapter = productAdapter

        // Load products from the database
        loadProducts()
    }

    private fun navigateToProduct(productDetails: ProductDetails) {
        val bundle = Bundle().apply {
            putString("productId", productDetails.productId)
            putString("productName", productDetails.productName)
            putString("productManufacturer", productDetails.productManufacturer)
            putString("productStock", productDetails.productStock.toString())
            putString("productMSRP", productDetails.productMSRP.toString())
            putString("productDescription", productDetails.productDescription)
            putString("productUsefulUrls", productDetails.productUsefulUrls)
            putString("productImage", productDetails.productImageUrl)
        }

        val productFragment = ProductFragment()
        productFragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, productFragment)
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
                // You can add error handling code here
            }
        })
    }
}