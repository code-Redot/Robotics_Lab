package com.example.robotics_lab

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.robotics_lab.databinding.FragmentAddEquipmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


////////////////////////////////////////////////////////////////////////////////////////////////////

class AddEquipmentFragment : Fragment() {

    private lateinit var binding: FragmentAddEquipmentBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGES_REQUEST = 6
    private val STORAGE_PERMISSION_REQUEST_CODE = 6
    private val photosList: MutableList<Uri> = mutableListOf()

    private val STORAGE_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEquipmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("equipments")

        binding.btnUploadPhoto.setOnClickListener {

            openGallery()

        }

        // Inside your fragment, use the Equipment data class as you previously did
        binding.btnSubmitEquipment.setOnClickListener {
            val equipmentName = binding.editTextEquipmentName.text.toString().trim()
            val manufacturer = binding.editTextManufacturer.text.toString().trim()
            val modelNumber = binding.editTextModelNumber.text.toString().trim()
            val equipmentDescription = binding.editTextEquipmentDescription.text.toString().trim()

            // Check if any of the fields are empty
            if (equipmentName.isEmpty() || manufacturer.isEmpty() || modelNumber.isEmpty() || equipmentDescription.isEmpty()) {
                // Display a message indicating that all fields are required
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                // All fields are filled, proceed with equipment submission
                // Generate a unique key for the equipment entry
                val equipmentKey = "EquId:" + database.child("equipments").push().key

                // Create a new equipment entry
                val equipmentEntry = Equipment(
                    equipmentKey,
                    equipmentName,
                    manufacturer,
                    modelNumber,
                    equipmentDescription,
                    photosList.map { it.toString() } as ArrayList<String>
                )

                // Save the equipment entry to the database
                if (equipmentKey != null) {
                    database.child(equipmentKey).setValue(equipmentEntry)
                        .addOnSuccessListener {
                            // Equipment entry saved successfully
                            // You can show a success message or navigate to another fragment/activity

                            // Call the uploadPhotos function with the actual equipment ID (equipmentKey)
                            uploadPhotos(equipmentKey, photosList)

                            // Navigate back to HomeFragment
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            // Error occurred while saving equipment entry
                            // Handle the error
                        }
                }
            }
        }


    }

    private fun checkStoragePermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PICK_IMAGES_REQUEST
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(Intent.createChooser(intent, "Select Photos"), PICK_IMAGES_REQUEST)
    }


    private fun uploadPhotos(equipmentId: String, photos: List<Uri>) {
        for (photoUri in photos) {
            val storageRef = FirebaseStorage.getInstance().reference.child("equipment_photos").child(equipmentId)

            // Generate a unique file name for each photo
            val photoFileName = UUID.randomUUID().toString()

            // Create a reference to the file in Firebase Storage
            val photoRef = storageRef.child(photoFileName)

            // Upload the photo to Firebase Storage
            photoRef.putFile(photoUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Photo uploaded successfully, you can retrieve the download URL if needed
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        // Save the photo URL in your database or perform other actions
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors while uploading the photo
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val photoUri = clipData.getItemAt(i).uri
                    photosList.add(photoUri)
                }
            } else if (data?.data != null) {
                val photoUri = data.data
                if (photoUri != null) {
                    photosList.add(photoUri)
                }
            }
        }
    }
}
