package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.adapters.GridItemAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentSearchBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentSearchStartBinding
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel

class SearchStartFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: FragmentSearchStartBinding

    private lateinit var context: Context

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    private lateinit var searchNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_start, container, false)

        searchNavController = findNavController()



        context = binding.root.context

        sharedPref = context.getSharedPreferences(MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()



        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        // Set the ViewModel in the binding
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner


        binding.searchBtn.setOnClickListener(View.OnClickListener {
            //Send To DoSearchFragment
            searchNavController.navigate(R.id.action_searchStartFragment_to_doSearchFragment)
        })

        val genres = arrayListOf("Podcast", "New Releases", "Hindi", "Punjabi", "Tamil", "Telugu", "Pop", "Indie", "Trending",
            "Love", "Mood", "Party", "Devotional", "Hip-Hop", "Chill", "Gaming", "K-pop", "Rock", "Instrumental", "Country",
            "Classical")

        // Create an ArrayAdapter to populate the GridView
        val gridAdapter = GridItemAdapter(context, genres)

        binding.gridView.adapter = gridAdapter

        return binding.root
    }

}