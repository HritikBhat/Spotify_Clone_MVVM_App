package com.hritikbhat.spotify_mvvm_app.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.hritikbhat.spotify_mvvm_app.Adapters.FavouriteSubFragmentAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouriteSubFragments.FavAlbumFragment
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouriteSubFragments.FavArtistFragment
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouriteSubFragments.FavPlaylistFragment
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouriteSubFragments.FavPodcastFragment

class FavouritesFragment : Fragment() {
    lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subFragments = listOf(
            FavPlaylistFragment(),
            FavArtistFragment(),
            FavAlbumFragment(),
            FavPodcastFragment()
        )

        val adapter = FavouriteSubFragmentAdapter(this, subFragments)
        binding.viewPager.adapter = adapter

        // Bind the ViewPager2 with the TabLayout
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if(position==0){
                tab.text = "Playlists"
            }
            if(position==1){
                tab.text = "Artists"
            }
            if(position==2){
                tab.text = "Albums"
            }
            if(position==3){
                tab.text = "Podcasts"
            }

        }.attach()
    }
}
