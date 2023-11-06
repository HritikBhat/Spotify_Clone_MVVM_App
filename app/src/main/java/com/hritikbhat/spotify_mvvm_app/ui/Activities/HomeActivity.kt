package com.hritikbhat.spotify_mvvm_app.ui.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.ActivityHomeBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentPlayingNowBinding
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.NowPlaying


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        val navController = findNavController(this, R.id.nav_host_fragment)

        setupWithNavController(binding.bottomNavigation,navController)

}

}