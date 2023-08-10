package com.example.robotics_lab

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.robotics_lab.databinding.ActivityHomePageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



////////////////////////////////////////////////////////////////////////////////////////////////////
class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityHomePageBinding
    private var userPrivilege: Int = 0

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        FirebaseApp.initializeApp(this)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Load the HomeFragment initially
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_content, HomeFragment())
                .commit()
        }
        // After the user successfully logs in, retrieve the user's privilege level from the Firebase Realtime Database
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            userPrivilege = user.privilegeLevel!!

                            // Store the userPrivilege in shared preferences or other storage mechanisms for later access
                            val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putInt("userPrivilege", userPrivilege).apply()

                            // Set up the custom app bar, navigation drawer, and navigation component
                            setupUI()
                        } else {
                            // Error: User data not found
                            Toast.makeText(
                                this@HomePageActivity,
                                "Error: User data not found",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } else {
                        // Error: User data not found
                        Toast.makeText(
                            this@HomePageActivity, "Error: User data not found", Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error occurred while querying Firebase
                    Toast.makeText(
                        this@HomePageActivity,
                        "Error occurred, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            })
        } else {
            // Error: User not logged in
            Toast.makeText(
                this@HomePageActivity, "Error: User not logged in", Toast.LENGTH_SHORT
            ).show()
            finish()
        }


    }

    private fun setupUI() {
        // Find the placeholder for the toolbar
        val toolbarPlaceholder = findViewById<View>(R.id.toolbar)

        // Set up the custom app bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set up the navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // Set up the Navigation Component with the navigation graph
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navHostFragment?.let {
            val navController = it.navController
            val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
            setupActionBarWithNavController(navController, appBarConfiguration)

            // Set up the bottom navigation listener
            bottomNavigation = findViewById(R.id.bottomNavigation)
            bottomNavigation.setOnNavigationItemSelectedListener { item ->
                navigateToDestination(item.itemId)
                true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Open or close the navigation drawer
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToDestination(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            //R.id.nav_item1 -> {
                // Create and return Fragment1 instance
            //}
            //R.id.nav_item2 -> {
                // Create and return Fragment2 instance
            //}
            R.id.btnAddProduct -> {
                // Create and return AddProductsFragment instance
                AddProductsFragment()
            }
            R.id.btnDeleteProduct -> {
                // Create and return DeleteProductFragment instance
                DeleteProductFragment()
            }
            else -> {
                // Default fragment (HomeFragment)
                HomeFragment()
            }
        }

        // Replace the content of the fragment_content container with the selected fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_content, fragment)
            .commit()
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // Handle navigation drawer item clicks and bottom navigation clicks
        navigateToDestination(menuItem.itemId)

        // Close the drawer after handling the item click
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        // Close the navigation drawer if it's open, or perform default back press behavior
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}