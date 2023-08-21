package com.example.robotics_lab


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.robotics_lab.databinding.FragmentProductBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


////////////////////////////////////////////////////////////////////////////////////////////////////
class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private var productId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize userPrivilegeLevel as null
        var userPrivilegeLevel: Int? = null

        // Extract data from arguments
        productId = arguments?.getString("productId")
        val productName = arguments?.getString("productName")
        val productManufacturer = arguments?.getString("productManufacturer")
        val productStock = arguments?.getString("productStock")
        val productMSRP = arguments?.getString("productMSRP")
        val productDescription = arguments?.getString("productDescription")
        val productUsefulUrls = arguments?.getString("productUsefulUrls")
        val productImage = arguments?.getString("productImage")

        // Set UI elements based on extracted data
        binding.textViewProductName.text = productName
        binding.textViewProductManufacturer.text = productManufacturer
        binding.textViewProductStock.text = getString(R.string.product_stock, productStock)
        binding.textViewProductMSRP.text = getString(R.string.product_msrp, productMSRP)
        binding.textViewProductDescription.text = productDescription
        binding.textViewProductUsefulUrls.text = productUsefulUrls

        // Load product image if available
        if (productImage != null && productImage.isNotEmpty()) {
            Glide.with(requireContext())
                .load(productImage)
                .placeholder(R.drawable.ic_product_image)
                .error(R.drawable.ic_error_image)
                .into(binding.imageViewProduct)
        }

        // Retrieve the user's privilege level from Firebase Realtime Database
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = userId?.let {
            FirebaseDatabase.getInstance().getReference("users").child(
                it
            )
        }

        if (databaseReference != null) {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userPrivilegeLevel = snapshot.child("privilegeLevel").getValue(Int::class.java)

                        // Update button visibility and status based on user privilege level
                        when (userPrivilegeLevel) {
                            0 -> {
                                // User privilege level is 0
                                // Show bottom bar and enable both buttons
                                binding.bottomBar.visibility = View.VISIBLE
                                binding.btnEditProduct.isEnabled = true
                                binding.btnDeleteProduct.isEnabled = true
                                binding.btnDeleteProduct.visibility = View.VISIBLE
                            }

                            1 -> {
                                // User privilege level is 1
                                // Show bottom bar, enable btnEditProduct, disable btnDeleteProduct, and hide it
                                binding.bottomBar.visibility = View.VISIBLE
                                binding.btnEditProduct.isEnabled = true
                                binding.btnDeleteProduct.isEnabled = false
                                binding.btnDeleteProduct.visibility = View.INVISIBLE
                            }

                            2 -> {
                                // User privilege level is 2
                                // Hide bottom bar and disable both buttons
                                binding.bottomBar.visibility = View.GONE
                                binding.btnEditProduct.isEnabled = false
                                binding.btnDeleteProduct.isEnabled = false
                            }

                            else -> {
                                // Handle other privilege levels or errors
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                }
            })
        }

        // Set click listener for delete button
        binding.btnDeleteProduct.setOnClickListener {
            if (userPrivilegeLevel == 0) {
                showDeleteConfirmationDialog(userPrivilegeLevel!!)
            } else {
                // User does not have sufficient privilege
                // Display an "unauthorized" warning here
                Toast.makeText(requireContext(), "Unauthorized action", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for edit button
        binding.btnEditProduct.setOnClickListener {
            navigateToEditProduct(userPrivilegeLevel!!)
        }
    }


    // Existing code for deleteProduct, showDeleteConfirmationDialog, and navigateToEditProduct functions

    private fun deleteProduct(productId: String?, userPrivilegeLevel: Int) {
        if (productId != null) {
            if (userPrivilegeLevel == 0) {
                val productRef: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("products").child(productId)
                productRef.removeValue()
                    .addOnSuccessListener {
                        // Product deleted successfully, you can show a success message or update the UI
                    }
                    .addOnFailureListener {
                        // Error occurred while deleting the product, handle the error
                    }
            } else {
                // User does not have sufficient privilege
                // Display an "unauthorized" warning here
                Toast.makeText(requireContext(), "Unauthorized action", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(userPrivilegeLevel: Int) {
        if (userPrivilegeLevel == 0) {
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteProduct(productId, userPrivilegeLevel)
                }
                .setNegativeButton("No", null)
                .create()

            alertDialog.show()
        } else {
            // User does not have sufficient privilege
            // Display an "unauthorized" warning here
            Toast.makeText(requireContext(), "Unauthorized action", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToEditProduct(userPrivilegeLevel: Int) {
        if (userPrivilegeLevel == 0 || userPrivilegeLevel == 1) {
            val args = Bundle()
            args.putString("productId", productId)

            val editFragment = EditProductFragment()
            editFragment.arguments = args

            val fragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, editFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else {
            // User does not have sufficient privilege
            // Display an "unauthorized" warning here
            Toast.makeText(requireContext(), "Unauthorized action", Toast.LENGTH_SHORT).show()
        }
    }

}
