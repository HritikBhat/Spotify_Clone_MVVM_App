package com.hritikbhat.spotify_mvvm_app.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FavouriteSubFragmentAdapter(fragment: Fragment, private val subFragments: List<Fragment>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = subFragments.size

    override fun createFragment(position: Int): Fragment = subFragments[position]
}

