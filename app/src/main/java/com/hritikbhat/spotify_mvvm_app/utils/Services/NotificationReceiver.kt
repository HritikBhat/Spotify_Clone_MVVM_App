package com.hritikbhat.spotify_mvvm_app.utils.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.exitApplication
import com.hritikbhat.spotify_mvvm_app.models.setSongPosition
import com.hritikbhat.spotify_mvvm_app.ui.fragments.NowPlaying
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper.BASE_URL
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity.Companion.viewModel
import kotlinx.coroutines.launch

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent ?. action){
            ApplicationClass.PREVIOUS -> prevNextSong(false,context!!)
            ApplicationClass.PLAY -> if(PlayActivity.mediaPlayerService?.mediaPlayer?.isPlaying == true) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true,context!!)
            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }

    }

    private fun playMusic(){
        Log.d("PlayPause Btn","Status: ${PlayActivity.isPlaying}")
        PlayActivity.isPlaying = true
        PlayActivity.mediaPlayerService !!. mediaPlayer !!. start()
        PlayActivity.mediaPlayerService !!. showNotification(R.drawable.ic_pause)
        PlayActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_pause)
        NowPlaying.binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_pause)
    }

    private fun pauseMusic(){
        PlayActivity.isPlaying = false
        PlayActivity.mediaPlayerService !!. mediaPlayer !!.pause()
        PlayActivity.mediaPlayerService !!. showNotification(R.drawable.ic_play)
        PlayActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_play)
        NowPlaying.binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_play)
    }



    private fun prevNextSong(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayActivity.mediaPlayerService!!.createMediaPlayer()
        Glide.with(context)
            . load("${BASE_URL +"data/img/playlist/"}${PlayActivity.songListArr[PlayActivity.position].albumId}.jpg")
            .apply(RequestOptions().placeholder(R.drawable.playlist_default_img).centerCrop())
            .into(PlayActivity.binding.playMusicImage)

        viewModel.viewModelScope.launch {
            viewModel.addRecentSong(FavSongQuery(PlayActivity.curr_passHash,PlayActivity.songListArr[PlayActivity.position].sid.toString()))
        }
        //PlayActivity.mediaPlayerService!!.showNotification(R.drawable.ic_pause)

        if (PlayActivity.songListArr[PlayActivity.position].isFav){
            PlayActivity.binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
        }
        else{
            PlayActivity.binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_unselected_white)
        }
        NowPlaying.binding.nowPlayingSongName.text = PlayActivity.songListArr[PlayActivity.position].sname

        PlayActivity.binding.songNameTT.text = PlayActivity.songListArr[PlayActivity.position].sname
        PlayActivity.binding.songArtistsTT.text = PlayActivity.songListArr[PlayActivity.position].artist_name_arr.joinToString(", ")
        playMusic()

    }

}