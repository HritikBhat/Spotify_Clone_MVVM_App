package com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavAlbumBinding

class FavAlbumFragment : Fragment() {
    private lateinit var binding: FragmentFavAlbumBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_album, container, false)

        return binding.root

    }





}