package com.example.robotics_lab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.robotics_lab.databinding.FragmentEquipmentDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EquipmentDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var equipmentId: String
    private lateinit var equipmentName: String
    private lateinit var equipmentManufacturer: String
    private lateinit var equipmentModelNumber: String
    private lateinit var equipmentDescription: String
    private val REQUEST_CODE_PERMISSION = 123
    private val REQUEST_CODE_IMAGE_PICK = 124
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageAdapter: EquipmentImageAdapter
    private val imageUrls = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEquipmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve equipment details from arguments
        equipmentId = requireArguments().getString("equipmentId", "")
        equipmentName = requireArguments().getString("equipmentName", "")
        equipmentManufacturer = requireArguments().getString("equipmentManufacturer", "")
        equipmentModelNumber = requireArguments().getString("equipmentModelNumber", "")
        equipmentDescription = requireArguments().getString("equipmentDescription", "")

        // Initialize Firebase Storage and Realtime Database
        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference

        // Initialize the RecyclerView for images
        val imageUrlList = mutableListOf<String>() // Replace with the actual list of image URLs
        imageAdapter = EquipmentImageAdapter(requireContext(), imageUrlList) // Initialize the adapter
        binding.recyclerViewImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewImages.adapter = imageAdapter

// Populate the UI with equipment details
        binding.textViewEquipmentName.text = equipmentName
        binding.textViewManufacturer.text = equipmentManufacturer
        binding.textViewModelNumber.text = equipmentModelNumber
        binding.textViewEquipmentDescription.text = equipmentDescription

// Request permission to access external storage
        requestPermission()

// Load and display images from Firebase Storage for the specified equipment
        loadImagesFromFirebase(equipmentId)

        // Request permission to access external storage
        requestPermission()

        // Load and display images from Firebase Storage for the specified equipment
        loadImagesFromFirebase(equipmentId)
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
        } else {
            // Permission is already granted, you can proceed to load images
            // or trigger the image selection process.
        }
    }

    private fun updateImageUrls(imageUrlsList: List<String>) {
        imageUrls.clear()
        imageUrls.addAll(imageUrlsList)
        imageAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed to load images
                // or trigger the image selection process.
            } else {
                // Permission denied, handle it (e.g., show a message or request again later).
            }
        }
    }

    private fun loadImagesFromFirebase(equipmentId: String) {
        // Use Firebase Realtime Database to fetch the image URLs for the specified equipment
        // Assuming your image URLs are stored under a child node "photos" for each equipment
        databaseReference.child("equipments").child(equipmentId).child("photos")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (imageSnapshot in dataSnapshot.children) {
                            val imageUrl = imageSnapshot.value.toString()
                            imageUrls.add(imageUrl)
                        }
                        imageAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error if needed
                }
            })
    }
}
