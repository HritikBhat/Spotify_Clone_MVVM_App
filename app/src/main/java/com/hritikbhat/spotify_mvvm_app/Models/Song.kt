package com.hritikbhat.spotify_mvvm_app.Models

import android.app.Activity
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.view.View
import com.hritikbhat.spotify_mvvm_app.ui.Activities.PlayActivity
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.NowPlaying
import kotlin.random.Random
import kotlin.system.exitProcess


data class Song(
    val sid: Int,
    val albumId: Int,
    val sname: String,
    val artist_name_arr: List<String>,
    var isFav: Boolean,
)

fun setSongPosition(increment: Boolean){
    if(PlayActivity.onShuffle){
        var newPos = Random.nextInt(PlayActivity.songListArr.size)
        while (newPos== PlayActivity.position){
            newPos = Random.nextInt(PlayActivity.songListArr.size)
        }
        PlayActivity.position =newPos
    }
    else{
        if(increment){
            if(PlayActivity.songListArr.size - 1 == PlayActivity.position)
                PlayActivity.position = 0
            else ++PlayActivity.position
        }else{
            if(0 == PlayActivity.position)
                PlayActivity.position = PlayActivity.songListArr.size-1
            else -- PlayActivity.position
        }
    }
}

fun exitApplication(){
    if(PlayActivity.mediaPlayerService != null){
        //PlayActivity.mediaPlayerService !!.audioManager.abandonAudioFocus(PlayActivity.mediaPlayerService
        PlayActivity.handler.removeCallbacksAndMessages(null)
        PlayActivity.mediaPlayerService !!.mediaPlayer!!.stop()
        PlayActivity.mediaPlayerService !!.mediaPlayer!!.release()
        PlayActivity.mediaPlayerService!!.mediaPlayer = null
        PlayActivity.mediaPlayerService !!.stopForeground(STOP_FOREGROUND_REMOVE)
//        PlayActivity.mediaPlayerService = null
        (PlayActivity.binding.root.context as Activity).finish()
        NowPlaying.binding.root.visibility = View.GONE
//        NowPlaying.binding.root.layoutParams.height = 0

    }

//    exitProcess(1)
}