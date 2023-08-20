package com.example.robotics_lab


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.robotics_lab.databinding.FragmentProductBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


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

        // Set click listener for delete button
        binding.btnDeleteProduct.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Set click listener for edit button
        binding.btnEditProduct.setOnClickListener {
            navigateToEditProduct()
        }
    }

    // Existing code for deleteProduct, showDeleteConfirmationDialog, and navigateToEditProduct functions

    private fun deleteProduct(productId: String?) {
        if (productId != null) {
            val productRef: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("products").child(productId)
            productRef.removeValue()
                .addOnSuccessListener {
                    // Product deleted successfully, you can show a success message or update the UI
                }
                .addOnFailureListener {
                    // Error occurred while deleting the product, handle the error
                }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { _, _ ->
                deleteProduct(productId)
            }
            .setNegativeButton("No", null)
            .create()

        alertDialog.show()
    }

    private fun navigateToEditProduct() {
        val args = Bundle()
        args.putString("productId", productId)

        val editFragment = EditProductFragment()
        editFragment.arguments = args

        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, editFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
