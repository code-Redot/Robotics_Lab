package com.example.robotics_lab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

        // Handle the "Upload Product to DB" button click
        buttonUploadToDB.setOnClickListener {
            uploadProductToDatabase()
        }

        return view
    }

    private fun uploadProductToDatabase() {
        val productName = editTextProductName.text.toString()
        val productStock = editTextProductStock.text.toString().toInt()
        val productMSRP = editTextProductMSRP.text.toString().toDouble()
        val productManufacturer = editTextProductManufacturer.text.toString()
        val productDescription = editTextProductDescription.text.toString()
        val productUsefulUrls = editTextProductUsefulUrls.text.toString()

        // Create a data model for the product
        val product = Product(
            productName,
            productStock,
            productMSRP,
            productManufacturer,
            productDescription,
            productUsefulUrls
        )

        // Upload the product data to the Firebase Realtime Database
        val productId = database.child("products").push().key
        productId?.let {
            database.child("products").child(it).setValue(product)
        }
    }
}
