package com.example.robotics_lab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.robotics_lab.databinding.FragmentHomeBinding
import com.google.firebase.database.*


////////////////////////////////////////////////////////////////////////////////////////////////////
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var equipmentAdapter: EquipmentAdapter
    private lateinit var database: DatabaseReference

    private val productDetailsList: MutableList<ProductDetails> = mutableListOf()
    private val equipmentList: MutableList<Equipment> = mutableListOf()

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
        database = FirebaseDatabase.getInstance().reference

        // Set up RecyclerView and adapter for products
        setupProductRecyclerView()

        // Set up RecyclerView and adapter for equipment
        setupEquipmentRecyclerView()

        // Load data
        loadData()
    }

    private fun setupProductRecyclerView() {
        binding.recyclerViewProductList.layoutManager = GridLayoutManager(requireContext(), 2)
        productAdapter = ProductAdapter(productDetailsList) { product ->
            navigateToProduct(product)
        }
        binding.recyclerViewProductList.adapter = productAdapter
    }

    private fun setupEquipmentRecyclerView() {
        binding.recyclerViewEquipmentList.layoutManager = LinearLayoutManager(requireContext())
        equipmentAdapter = EquipmentAdapter(requireActivity(), equipmentList) { equipment ->
            // Handle item click here, for example, navigate to equipment details
            navigateToEquipmentDetails(equipment)
        }
        binding.recyclerViewEquipmentList.adapter = equipmentAdapter
    }

    private fun loadData() {
        loadProducts()
        loadEquipment()
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

    private fun navigateToEquipmentDetails(equipment: Equipment) {
        val bundle = Bundle().apply {
            putString("equipmentId", equipment.equipmentId)
            putString("equipmentName", equipment.equipmentName)
            putString("equipmentManufacturer", equipment.manufacturer)
            putString("equipmentModelNumber", equipment.modelNumber)
            putString("equipmentDescription", equipment.equipmentDescription)

            // You can also pass the photos if needed
            putStringArrayList("equipmentPhotos", equipment.photos)
        }

        val equipmentDetailsFragment = EquipmentDetailsFragment()
        equipmentDetailsFragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, equipmentDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



    private fun loadProducts() {
        val productsRef = database.child("products")
        productsRef.addValueEventListener(object : ValueEventListener {
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
                Log.e(TAG, "Error loading product data: ${error.message}")
                // Handle database error for products
            }
        })
    }

    private fun loadEquipment() {
        val equipmentRef = database.child("equipments")
        equipmentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                equipmentList.clear()
                for (equipmentSnapshot in snapshot.children) {
                    val equipment = equipmentSnapshot.getValue(Equipment::class.java)
                    equipment?.let {
                        equipmentList.add(it)
                    }
                }
                equipmentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading equipment data: ${error.message}")
                // Handle database error for equipment
            }
        })
    }


    companion object {
        private const val TAG = "HomeFragment"
    }


}