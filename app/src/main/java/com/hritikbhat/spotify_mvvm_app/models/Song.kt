package com.hritikbhat.spotify_mvvm_app.models

import android.app.Activity
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
import com.hritikbhat.spotify_mvvm_app.ui.fragments.NowPlaying
import kotlin.random.Random


data class Song(
    val sid: Int,
    val albumId: Int,
    val sname: String,
    val artist_name_arr: List<String>,
    var isFav: Boolean,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(sid)
        parcel.writeInt(albumId)
        parcel.writeString(sname)
        parcel.writeStringList(artist_name_arr)
        parcel.writeByte(if (isFav) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}

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
        PlayActivity.updateSeekBarJob!!.cancel()
        PlayActivity.mediaPlayerService !!.mediaPlayer!!.stop()
        PlayActivity.mediaPlayerService !!.mediaPlayer!!.release()
        PlayActivity.mediaPlayerService!!.mediaPlayer = null
        PlayActivity.mediaPlayerService !!.stopForeground(STOP_FOREGROUND_REMOVE)
//        PlayActivity.mediaPlayerService = null
//        (PlayActivity.binding.root.context as Activity).finish()
        NowPlaying.binding.root.visibility = View.GONE
//        NowPlaying.binding.root.layoutParams.height = 0

    }

//    exitProcess(1)
}