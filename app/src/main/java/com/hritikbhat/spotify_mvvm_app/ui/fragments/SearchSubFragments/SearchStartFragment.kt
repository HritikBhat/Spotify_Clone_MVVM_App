package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.adapters.GridItemAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentSearchStartBinding
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel

class SearchStartFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private var _binding: FragmentSearchStartBinding?=null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currPassHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_start, container, false)
        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        binding.searchBtn.setOnClickListener{
            //Send To DoSearchFragment
            findNavController().navigate(R.id.action_searchStartFragment_to_doSearchFragment)
        }

        val genres = arrayListOf("Podcast", "New Releases", "Hindi", "Punjabi", "Tamil", "Telugu", "Pop", "Indie", "Trending",
            "Love", "Mood", "Party", "Devotional", "Hip-Hop", "Chill", "Gaming", "K-pop", "Rock", "Instrumental", "Country",
            "Classical")

        // Create an ArrayAdapter to populate the GridView
        val gridAdapter = GridItemAdapter(requireContext(), genres)

        binding.gridView.adapter = gridAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
        _binding = null
    }

}