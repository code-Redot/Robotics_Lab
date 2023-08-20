package com.example.robotics_lab

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


////////////////////////////////////////////////////////////////////////////////////////////////////
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val SPLASH_SCREEN_TIMEOUT = 2700L // 2.7 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        setContentView(R.layout.activity_splash_screen)

        // Set a delay for the splash screen and navigate to the login screen
        Handler().postDelayed({
            val intent = Intent(this, BufferActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_TIMEOUT)
    }
}


