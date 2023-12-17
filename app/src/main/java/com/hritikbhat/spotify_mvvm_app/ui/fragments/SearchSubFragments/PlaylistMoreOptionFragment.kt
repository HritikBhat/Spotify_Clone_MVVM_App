package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentPlaylistMoreOptionBinding


class PlaylistMoreOptionFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistMoreOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_more_option, container, false)

        return binding.root
    }

}