package com.hritikbhat.spotify_mvvm_app.ui.Fragments.BaseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hritikbhat.spotify_mvvm_app.adapters.FavouriteSubFragmentAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments.FavAlbumFragment
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments.FavArtistFragment
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments.FavPlaylistFragment
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments.FavPodcastFragment

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!


    companion object{
        var viewPager: ViewPager2? = null
        var tabLayout: TabLayout? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
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
            tab.text = when (position) {
                0 -> "Playlists"
                1 -> "Artists"
                2 -> "Albums"
                else -> "Podcasts"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Release the binding
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager = null
        tabLayout = null
    }
}

