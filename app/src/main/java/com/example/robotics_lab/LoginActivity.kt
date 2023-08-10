package com.example.robotics_lab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.robotics_lab.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

////////////////////////////////////////////////////////////////////////////////////////////////////
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            onSignInButtonClick()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, you can redirect to the home activity
            redirectToHomeActivity(currentUser)
        }
    }

    private fun redirectToHomeActivity(user: FirebaseUser) {
        // Start the HomePageActivity or any other desired activity
        val intent = Intent(this, HomePageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun onSignInButtonClick() {
        // Get email and password from the input fields
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()

        // Validate the email and password
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Sign in with email and password using Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success
                    val user = auth.currentUser
                    if (user != null) {
                        redirectToHomeActivity(user)
                    }
                } else {
                    // Login failed
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Log the error message for debugging
                Log.e(TAG, "Authentication failed: ${e.message}", e)
                Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}