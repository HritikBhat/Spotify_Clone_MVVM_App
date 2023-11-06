package com.hritikbhat.spotify_mvvm_app.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hritikbhat.spotify_mvvm_app.R

class PremiumFragment : Fragment() {

    companion object {
        fun newInstance() = PremiumFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_premium, container, false)
        return view
    }

}