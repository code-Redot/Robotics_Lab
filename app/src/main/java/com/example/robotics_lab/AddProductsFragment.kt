package com.example.robotics_lab

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

////////////////////////////////////////////////////////////////////////////////////////////////////
class AddProductsFragment : Fragment() {

    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductStock: EditText
    private lateinit var editTextProductMSRP: EditText
    private lateinit var editTextProductManufacturer: EditText
    private lateinit var editTextProductDescription: EditText
    private lateinit var editTextProductUsefulUrls: EditText
    private lateinit var buttonUploadPictures: Button
    private lateinit var buttonUploadToDB: Button
    private lateinit var database: DatabaseReference
    private var productImageUrl: String? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        // Initialize the views
        editTextProductName = view.findViewById(R.id.editTextProductName)
        editTextProductStock = view.findViewById(R.id.editTextProductStock)
        editTextProductMSRP = view.findViewById(R.id.editTextProductMSRP)
        editTextProductManufacturer = view.findViewById(R.id.editTextProductManufacturer)
        editTextProductDescription = view.findViewById(R.id.editTextProductDescription)
        editTextProductUsefulUrls = view.findViewById(R.id.editTextProductUsefulUrls)
        buttonUploadPictures = view.findViewById(R.id.buttonUploadPictures)
        buttonUploadToDB = view.findViewById(R.id.buttonUploadToDB)

        // Set input type for stock and MSRP EditText to only allow numeric input
        editTextProductStock.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        editTextProductMSRP.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        // Initialize the Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().reference

        // Handle the "Upload Pictures" button click
        buttonUploadPictures.setOnClickListener {
            openGallery()
        }

        // Handle the "Upload Product to DB" button click
        buttonUploadToDB.setOnClickListener {
            uploadProductToDatabase()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                productImageUrl = selectedImageUri.toString()
            }
        }
    }

    private fun uploadProductToDatabase() {
        val productName = editTextProductName.text.toString().trim()
        val productStockStr = editTextProductStock.text.toString().trim()
        val productMSRPStr = editTextProductMSRP.text.toString().trim()
        val productManufacturer = editTextProductManufacturer.text.toString().trim()
        val productDescription = editTextProductDescription.text.toString().trim()
        val productUsefulUrls = editTextProductUsefulUrls.text.toString().trim()

        // Validate required fields
        if (productName.isEmpty() || productStockStr.isEmpty() || productMSRPStr.isEmpty() || productManufacturer.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate numerical values for productStock and productMSRP
        val productStock: Int
        val productMSRP: Double

        try {
            productStock = productStockStr.toInt()
            productMSRP = productMSRPStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Invalid numerical value entered", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique product ID with the "ProdId:" prefix
        val productId = "ProdId:" + database.child("products").push().key

        // Create a ProductDetails object
        val productDetails = ProductDetails(
            productId = productId,
            productName = productName,
            productStock = productStock,
            productMSRP = productMSRP,
            productManufacturer = productManufacturer,
            productDescription = productDescription,
            productUsefulUrls = productUsefulUrls,
            productImageUrl = productImageUrl ?: ""
        )

        // Upload the product data to the Firebase Realtime Database
        productId?.let {
            database.child("products").child(productId).setValue(productDetails)
                .addOnSuccessListener {
                    // Product uploaded successfully
                    Toast.makeText(requireContext(), "The product has been uploaded", Toast.LENGTH_SHORT).show()

                    // Navigate back to HomeFragment
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    // Failed to upload product
                    Toast.makeText(requireContext(), "Failed to upload the product, please try again", Toast.LENGTH_SHORT).show()
                }
        }
    }

}

