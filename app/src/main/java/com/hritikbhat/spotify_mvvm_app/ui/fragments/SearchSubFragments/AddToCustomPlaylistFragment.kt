package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentAddToCustomPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentShowPlaylistSongsBinding
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import kotlinx.coroutines.launch

class AddToCustomPlaylistFragment : Fragment(),AddToPlaylistAdapter.OnItemClickListener {

    private lateinit var binding: FragmentAddToCustomPlaylistBinding

    private val addToPlaylistAdapter = AddToPlaylistAdapter()

    private lateinit var viewModel: SearchViewModel

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_playlist_songs, container, false)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        sharedPref = requireContext().getSharedPreferences(MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        //Set up AddToPlaylistRC
        binding.addToPlaylistRC.layoutManager = LinearLayoutManager(context)
        binding.addToPlaylistRC.adapter = addToPlaylistAdapter
        addToPlaylistAdapter.setOnItemClickListener(this)

        return inflater.inflate(R.layout.fragment_add_to_custom_playlist, container, false)
    }

    private suspend  fun setInitForAddToPlay(sid: String) {
        //binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
        //Go to add to playlist tab

        var operationResult: OperationResult<favPlaylists> =
            viewModel.getUserCustomFavPlaylist(FavPlaylistQuery(curr_passHash,"-1"))

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : favPlaylists = operationResult.data


                addToPlaylistAdapter.setPlaylistItems(favTransactionResp.favPlaylists,sid)

            }
            is OperationResult.Error -> {
                // An error occurred, handle the error
                val errorMessage = operationResult.message
                Log.e("ERROR", errorMessage)
                // Handle the error, for example, display an error message to the user
            }
        }
    }

    override fun onSelectingAddToPlaylistItemClick(plid: String, sid: String) {
        //AddFavSong
        viewModel.viewModelScope.launch {
            addSongToCustomPlaylist(curr_passHash,plid,sid)
        }
    }

    private suspend fun addSongToCustomPlaylist(curr_passHash: String, plid: String, sid: String) {
        var operationResult: OperationResult<FavTransactionResp> = viewModel.addSongToPlaylist(
            AddSongPlaylistQuery(curr_passHash,plid,sid)
        )

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }
//                binding.searchStartLayout.visibility = View.GONE
//                binding.searchRc.visibility = View.GONE
//                binding.notFoundLayout.visibility = View.GONE
//                binding.songMoreOptionLayout.visibility=View.GONE
//                binding.addToPlaylistRC.visibility=View.GONE
//                binding.playlistRC.visibility = View.VISIBLE
                // Process searchList here
            }
            is OperationResult.Error -> {
                // An error occurred, handle the error
                val errorMessage = operationResult.message
                Log.e("ERROR", errorMessage)
                // Handle the error, for example, display an error message to the user
            }
        }
    }
}