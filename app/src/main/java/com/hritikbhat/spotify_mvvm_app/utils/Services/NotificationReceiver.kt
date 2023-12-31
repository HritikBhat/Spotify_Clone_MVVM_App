package com.hritikbhat.spotify_mvvm_app.utils.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hritikbhat.spotify_mvvm_app.models.exitApplication
import com.hritikbhat.spotify_mvvm_app.models.setSongPosition
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.BaseFragment.NowPlaying
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent ?. action){
            ApplicationClass.PREVIOUS -> prevNextSong(false)
            ApplicationClass.PLAY -> if(PlayActivity.mediaPlayerService?.mediaPlayer?.isPlaying == true) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true)
            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }
    }

    private fun playMusic(){
        Log.d("MediaPlayerStatus","Started From Notification")
        PlayActivity.isPlaying = true
        PlayActivity.mediaPlayerService !!. mediaPlayer !!. start()
        PlayActivity.mediaPlayerService !!. showNotification(R.drawable.ic_pause)
        NowPlaying.binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_pause)
    }

    private fun pauseMusic(){
        Log.d("MediaPlayerStatus","Stopped From Notification")
        PlayActivity.isPlaying = false
        PlayActivity.mediaPlayerService !!. mediaPlayer !!.pause()
        PlayActivity.mediaPlayerService !!. showNotification(R.drawable.ic_play)
        NowPlaying.binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_play)
    }



    private fun prevNextSong(increment: Boolean){
        setSongPosition(increment = increment)
        PlayActivity.mediaPlayerService!!.createMediaPlayer()
        NowPlaying.binding.nowPlayingSongName.text = PlayActivity.songListArr[PlayActivity.position].sname
        playMusic()
    }

}