package com.hritikbhat.spotify_mvvm_app.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        val navController = findNavController(this, R.id.nav_host_fragment)

        setupWithNavController(binding.bottomNavigation,navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}