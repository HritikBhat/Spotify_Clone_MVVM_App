package com.hritikbhat.spotify_mvvm_app.ui.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.ActivityAddToCustomPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
import com.hritikbhat.spotify_mvvm_app.viewModels.FavouritesViewModel
import kotlinx.coroutines.launch

class AddToCustomPlaylistActivity : AppCompatActivity(),AddToPlaylistAdapter.OnItemClickListener {
    private lateinit var binding: ActivityAddToCustomPlaylistBinding
    private val addToPlaylistAdapter = AddToPlaylistAdapter()
    private lateinit var sid:String
    private lateinit var viewModel: FavouritesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_to_custom_playlist)
        addToPlaylistAdapter.setOnItemClickListener(this)

        val bundle :Bundle ?=intent.extras

        sid = bundle?.getString("sid").toString()

        viewModel = ViewModelProvider(this)[FavouritesViewModel::class.java]


        viewModel.viewModelScope.launch {
            setInitForAddToPlay(sid)
        }


    }

    private suspend  fun setInitForAddToPlay(sid: String) {
        //binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)

        val operationResult: OperationResult<favPlaylists> =
            viewModel.getUserCustomFavPlaylist(FavPlaylistQuery(PlayActivity.currPassHash,"-1"))

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : favPlaylists = operationResult.data

                addToPlaylistAdapter.setPlaylistItems(favTransactionResp.favPlaylists,sid)

                binding.addToPlaylistRC.layoutManager = LinearLayoutManager(this)
                binding.addToPlaylistRC.adapter = addToPlaylistAdapter

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
            addSongToCustomPlaylist(PlayActivity.currPassHash,plid,sid)
        }
    }

    override fun onSelectingBackButton() {
        //Nothing here
    }


    private suspend fun addSongToCustomPlaylist(currPassHash: String, plid: String, sid: String) {
        val operationResult: OperationResult<FavTransactionResp> = viewModel.addSongToPlaylist(
            AddSongPlaylistQuery(currPassHash,plid,sid)
        )

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    Toast.makeText(this,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }

                finish()
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