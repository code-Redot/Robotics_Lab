package com.example.robotics_lab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.robotics_lab.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener


////////////////////////////////////////////////////////////////////////////////////////////////////
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var usersRef: DatabaseReference
    private lateinit var userCounterRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        usersRef = FirebaseDatabase.getInstance().getReference("users")
        userCounterRef = FirebaseDatabase.getInstance().getReference("userCounter")
    }

    fun onSignUpButtonClick(view: View) {
        val name = binding.editTextName.text.toString().trim()
        var email = binding.editTextEmail.text.toString().trim().toLowerCase()
        val password = binding.editTextPassword.text.toString()
        val privilegeLevel = 2 // Set the default privilege level to 2

        // Validate email using the isValidEmail function
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = user.uid
                        val userIdWithPrefix = "User:$userId"
                        val newUser = User(userIdWithPrefix, name, email, password, privilegeLevel)

                        // Upload the user data to Firebase Realtime Database
                        usersRef.child(userId).setValue(newUser)
                            .addOnSuccessListener {
                                // User data saved successfully
                                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                // Explicit Intent to navigate back to BufferActivity
                                val intent = Intent(this, BufferActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                // Failed to save user data
                                Toast.makeText(this, "Sign up failed, please try again", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Sign-up failed
                    Toast.makeText(this, "Sign up failed, please try again", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex(pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return emailRegex.matches(email)
    }
}