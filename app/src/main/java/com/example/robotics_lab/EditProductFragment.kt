package com.example.robotics_lab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class EditProductFragment : Fragment() {

    private lateinit var productId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_product, container, false)

        // Retrieve productId from arguments
        productId = requireArguments().getString("productId") ?: ""

        // Initialize UI elements
        val nameEditText = view.findViewById<EditText>(R.id.editTextProductName)
        val manufacturerEditText = view.findViewById<EditText>(R.id.editTextProductManufacturer)
        val stockEditText = view.findViewById<EditText>(R.id.editTextProductStock)
        val msrpEditText = view.findViewById<EditText>(R.id.editTextProductMSRP)
        val descriptionEditText = view.findViewById<EditText>(R.id.editTextProductDescription)
        val urlsEditText = view.findViewById<EditText>(R.id.editTextProductUsefulUrls)
        val saveButton = view.findViewById<Button>(R.id.btnSaveChanges)

        // Handle Save button click
        saveButton.setOnClickListener {
            val editedName = nameEditText.text.toString()
            val editedManufacturer = manufacturerEditText.text.toString()
            val editedStock = stockEditText.text.toString().toInt()
            val editedMSRP = msrpEditText.text.toString().toDouble()
            val editedDescription = descriptionEditText.text.toString()
            val editedUrls = urlsEditText.text.toString()

            updateProductInDatabase(productId, editedName, editedManufacturer, editedStock, editedMSRP, editedDescription, editedUrls)
        }

        return view
    }

    private fun updateProductInDatabase(productId: String, name: String, manufacturer: String, stock: Int, msrp: Double, description: String, urls: String) {
        val productRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId)

        val updatedProduct = ProductDetails(productId, name, manufacturer, stock, msrp, description, urls)

        productRef.setValue(updatedProduct)
            .addOnSuccessListener {
                // Product details updated successfully
                // You can show a success message or update the UI as needed
                Log.d(TAG, "Product details updated successfully")

                // Replace the current fragment with FragmentHome
                val homeFragment = HomeFragment()
                val fragmentTransaction = requireFragmentManager().beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, homeFragment)
                fragmentTransaction.addToBackStack(null) // Optional, if you want to add to back stack
                fragmentTransaction.commit()
            }
            .addOnFailureListener {
                // Error occurred while updating the product details
                // Handle the error and show an error message if needed
                Log.e(TAG, "Error updating product details: ${it.message}")
                // Example: You can show a toast message or update an error UI indicator here
            }

    }

}
