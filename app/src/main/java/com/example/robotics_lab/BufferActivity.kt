package com.example.robotics_lab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.robotics_lab.databinding.ActivityBufferBinding


////////////////////////////////////////////////////////////////////////////////////////////////////
class BufferActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBufferBinding // View Binding instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBufferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners for login and signup buttons
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Prevent going back to the buffer page
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish() // Prevent going back to the buffer page
        }
    }
}