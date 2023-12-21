package com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavArtistViewModel
import com.hritikbhat.spotify_mvvm_app.R

class FavArtistFragment : Fragment() {

    companion object {
        fun newInstance() = FavArtistFragment()
    }

    private lateinit var viewModel: FavArtistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this)[FavArtistViewModel::class.java]


        return inflater.inflate(R.layout.fragment_fav_artist, container, false)
    }
}