package com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.FavPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.CustomPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistsX
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import kotlinx.coroutines.launch

class FavPlaylistFragment : Fragment() {
    private lateinit var viewModel: FavPlaylistViewModel
    private lateinit var binding: FragmentFavPlaylistBinding

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0


    private val handler = Handler(Looper.myLooper()!!)
    private lateinit var context: Context

    private val playlistAdapter = PlaylistAdapter()
    private val addToPlaylistAdapter = AddToPlaylistAdapter()

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    private var plqAname = ""
    private var plqPname = ""
    private var plqPType = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_playlist, container, false)





        return binding.root

    }
}