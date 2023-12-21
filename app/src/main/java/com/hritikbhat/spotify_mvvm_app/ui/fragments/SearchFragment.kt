package com.hritikbhat.spotify_mvvm_app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hritikbhat.spotify_mvvm_app.R
import androidx.databinding.DataBindingUtil
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentSearchBinding

class SearchFragment : Fragment(){

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        return binding.root
    }
}
