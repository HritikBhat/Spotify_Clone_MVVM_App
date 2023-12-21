package com.hritikbhat.spotify_mvvm_app.ui.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.TransactionTypes
import com.hritikbhat.spotify_mvvm_app.databinding.ActivitySongMoreOptionPlayBinding
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity.Companion.isFav
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity.Companion.songListArr
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.viewModels.FavouritesViewModel
import kotlinx.coroutines.launch

class SongMoreOptionPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongMoreOptionPlayBinding

    private lateinit var songMoreOptionIntent: Intent
    private var pos = -1
    private lateinit var viewModel: FavouritesViewModel


    override fun onResume() {
        //Checks if song isFav or not
        binding.songOptionImg.setImageResource(if(isFav) R.drawable.ic_fav_selected_white else R.drawable.ic_fav_unselected_white)
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_song_more_option_play)

        binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)

        viewModel = ViewModelProvider(this)[FavouritesViewModel::class.java]

        this.songMoreOptionIntent = intent

        val bundle :Bundle ?=songMoreOptionIntent.extras

        pos = bundle?.getInt("pos") ?: -1


        val isFavSong = songListArr[pos].isFav

        Log.d("CHEEZY NOTIFICATION","isFavSong Flag: $isFavSong")


        binding.songName.text = songListArr[pos].sname
        val plURL = RetrofitHelper.BASE_URL +"data/img/playlist/${songListArr[pos].albumId}.jpg"

        Glide.with(binding.root.context)
            .load(plURL).thumbnail()
            .into(binding.songAlbumImg)

        binding.songArtists.text=songListArr[pos].artist_name_arr.joinToString(", ")

        if (isFavSong){
            binding.songOptionImg.setImageResource(R.drawable.ic_fav_selected_white)
        }

        binding.addToPlaylistSong.setOnClickListener {
            val sid = songListArr[pos].sid
            val addToCustomPlayListIntent = Intent(this , AddToCustomPlaylistActivity::class.java)
            addToCustomPlayListIntent.putExtra("sid",sid.toString())
            startActivity(addToCustomPlayListIntent)
        }

        binding.likeSong.setOnClickListener {

            if (isFavSong) {

                //DeleteFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(
                        FavSongQuery(PlayActivity.currPassHash, songListArr[pos].sid.toString()),
                        TransactionTypes.DELETE_TRANSACTION
                    )
                    isFav = false
                    songListArr[pos].isFav = isFav

                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
                }
            } else {

                //AddFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(
                        FavSongQuery(PlayActivity.currPassHash, songListArr[pos].sid.toString()),
                        TransactionTypes.INSERT_TRANSACTION
                    )
                    isFav = true
                    songListArr[pos].isFav = isFav
                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_selected_white)
                }
            }
        }
        
    }

    private suspend  fun setFavSongStatus(fQ: FavSongQuery, transType:TransactionTypes){
        val operationResult: OperationResult<FavTransactionResp> = if (transType==TransactionTypes.INSERT_TRANSACTION){
            viewModel.addFavSong(fQ)
        } else{
            viewModel.removeFavSong(fQ)
        }

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    Toast.makeText(applicationContext,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(applicationContext,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }

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