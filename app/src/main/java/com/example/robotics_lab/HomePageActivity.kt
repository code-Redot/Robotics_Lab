package com.example.robotics_lab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.robotics_lab.databinding.ActivityHomePageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


////////////////////////////////////////////////////////////////////////////////////////////////////

class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityHomePageBinding
    private var userPrivilege: Int = 0

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize FirebaseApp
        FirebaseApp.initializeApp(this)

        // Initialize the binding layout
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the NavHostFragment and NavController
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Load user data and set up UI
        loadUserDataAndSetupUI()

        // Find the Toolbar and set it as the support action bar
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun loadUserDataAndSetupUI() {
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

                            // Set up the UI components
                            setupUI()

                            // Set up the initial fragment (HomeFragment) using NavController
                            if (userPrivilege == 0) {
                                navController.navigate(R.id.homeFragment)
                            }
                        } else {
                            // Error: User data not found
                            handleUserDataError()
                        }
                    } else {
                        // Error: User data not found
                        handleUserDataError()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error occurred while querying Firebase
                    handleUserDataError()
                }
            })
        } else {
            // Error: User not logged in
            handleUserDataError()
        }
    }

    private fun handleUserDataError() {
        Toast.makeText(
            this@HomePageActivity,
            "Error: Unable to retrieve user data",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun setupUI() {
        // Set up the custom app bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set up the navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)

        // Fetch the current signed-in user's data and update the views
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            // Update the views with the user's data
                            val headerView = navigationView.getHeaderView(0)
                            val titleTextView = headerView.findViewById<TextView>(R.id.titleTextView)
                            val subtitleTextView = headerView.findViewById<TextView>(R.id.subtitleTextView)

                            // Set "name" from the database as the titleTextView text
                            titleTextView.text = user.name

                            // Set "email" from the database as the subtitleTextView text
                            subtitleTextView.text = user.email

                            // Note: You can add views to your layout for user information and update them here
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }

        navigationView.setNavigationItemSelectedListener(this)

        // Inflate the menu based on userPrivilege
        navigationView.menu.clear()
        navigationView.inflateMenu(if (userPrivilege == 0) R.menu.activity_main_drawer else R.menu.activity_main_drawer_non_admin)

        // Set up the Navigation Component with the navigation graph
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navHostFragment?.let {
            val navController = it.navController
            val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
            setupActionBarWithNavController(navController, appBarConfiguration)
        }

        // Fill the fragment_container with HomeFragment
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homeFragment)
            .commit()
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
            R.id.nav_add_product -> {
                // Handle "Add Product" selection
                val fragment = AddProductsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_host, fragment)
                    .commit()
                true
            }
            R.id.nav_add_equipment -> {
                // Handle "Add Equipment" selection
                val fragment = AddEquipmentFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_host, fragment)
                    .commit()
                true
            }
            R.id.action_log_out -> {
                // Handle "Log Out" selection
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun navigateToDestination(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.nav_add_product -> {
                // Create and return AddProductsFragment instance
                AddProductsFragment()
            }
            R.id.nav_add_equipment -> {
                // Create and return AddEquipmentFragment instance
                AddEquipmentFragment()
            }
            else -> {
                // Handle other navigation items
                HomeFragment()
            }
        }

        // Replace the content of the fragment_container within content_host
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // Handle navigation drawer item clicks and bottom navigation clicks
        when (menuItem.itemId) {
            R.id.nav_add_product -> {
                // Create and return AddProductsFragment instance
                val fragment = AddProductsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_add_equipment -> {
                // Create and return AddEquipmentFragment instance
                val fragment = AddEquipmentFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            R.id.action_log_out -> {
                // Handle the "Log Out" action here
                // For example, you can sign the user out and navigate to the login screen
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> {
                // Handle other navigation items
                navigateToDestination(menuItem.itemId)
            }
        }

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
