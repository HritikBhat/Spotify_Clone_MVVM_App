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
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavAlbumViewModel
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavAlbumBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import kotlinx.coroutines.launch

class FavAlbumFragment : Fragment() {
//    private lateinit var viewModel: FavAlbumViewModel
    private lateinit var binding: FragmentFavAlbumBinding
//    private lateinit var binding2: FragmentFavouritesBinding
//
//    private val INSERTTRANSACTION =1
//    private  val DELETETRANSACTION=0
//
//    private val handler = Handler(Looper.myLooper()!!)
//    private lateinit var context: Context
//
//    private val favPlaylistRCAdapter = FavPlaylistAdapter()
//    private val playlistAdapter = PlaylistAdapter()
//    private val addToPlaylistAdapter = AddToPlaylistAdapter()
//
//    private lateinit var sharedPref: SharedPreferences
//
//    private val MY_PREFS_NAME: String = "MY_PREFS"
//    private lateinit var curr_passHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_album, container, false)




//        binding2 = FavouritesFragment.binding
//        context = binding.root.context
//
//        sharedPref = context.getSharedPreferences(
//            MY_PREFS_NAME,
//            AppCompatActivity.MODE_PRIVATE
//        )
//        curr_passHash = sharedPref.getString("passHash", "").toString()

//        favPlaylistRCAdapter.setOnItemClickListener(this)
//        playlistAdapter.setOnItemClickListener(this)
//        addToPlaylistAdapter.setOnItemClickListener(this)

//        viewModel = ViewModelProvider(this).get(FavAlbumViewModel::class.java)

//        activity?.onBackPressedDispatcher?.addCallback(
//            viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if(binding.playlistRC.visibility==View.VISIBLE){
//                        viewModel.viewModelScope.launch {
//                            getUserFavPlaylist()
//                        }
//                        if (favPlaylistRCAdapter.itemCount==0){
//                            binding.noFavouriteLayout.visibility=View.VISIBLE
//                            binding.favAlbumRC.visibility = View.GONE
//                        }else{
//                            binding.noFavouriteLayout.visibility=View.GONE
//                            binding.favAlbumRC.visibility = View.VISIBLE
//                        }
//                        binding2.tabLayout.visibility = View.VISIBLE
//                        binding2.viewPager.isUserInputEnabled = true
//                        binding.playlistRC.visibility = View.GONE
//                    }
//                }
//            }
//        )





        // Set up RecyclerView
//        binding.favAlbumRC.layoutManager = LinearLayoutManager(context)
//        binding.favAlbumRC.adapter = favPlaylistRCAdapter

//        binding.playlistRC.layoutManager = LinearLayoutManager(context)
//        binding.playlistRC.adapter = playlistAdapter
//
//        binding.addToPlaylistRC.layoutManager = LinearLayoutManager(context)
//        binding.addToPlaylistRC.adapter = addToPlaylistAdapter


        return binding.root

    }





}