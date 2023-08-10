package com.example.robotics_lab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView


////////////////////////////////////////////////////////////////////////////////////////////////////
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Mock data for testing
        val productList = listOf(
            Product("Product 1", 1, 32132.0, "meaw", "the generic loriaum lepron james what ever ", "https://www.google.com"),
            Product("Product 2", 2, 4532.0, "mew", "the generic loriaum lepron james what ever ", "https://www.google.com"),
            Product("Product 3", 3, 213556.0, "maw", "the generic loriaum lepron james what ever ", "https://www.google.com"),
            Product("Product 4", 4, 332.0, "lmew", "the generic loriaum lepron james what ever ", "https://www.google.com"),
            // Add more mock data as needed
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewProductList)
        recyclerView.adapter = ProductAdapter(productList)

        return view
    }
}