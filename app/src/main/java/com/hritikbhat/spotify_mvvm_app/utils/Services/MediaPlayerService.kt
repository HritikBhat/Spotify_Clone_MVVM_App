package com.hritikbhat.spotify_mvvm_app.utils.Services

// MediaPlayerService.kt

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper.BASE_URL
import com.hritikbhat.spotify_mvvm_app.ui.activities.HomeActivity
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
import kotlinx.coroutines.launch
import kotlin.random.Random


class MediaPlayerService: Service() {
    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }
    private lateinit var albumBitmap: Bitmap
    private var playPauseBtnDrawable = -1
    private var prevPendingIntent: PendingIntent? =null
    private var playPendingIntent: PendingIntent? =null
    private var nextPendingIntent: PendingIntent? =null
    private var exitPendingIntent: PendingIntent? =null
    private var contextIntent: Intent? =null

    inner class MyBinder:Binder() {
            fun currentService(): MediaPlayerService {
                return this@MediaPlayerService
            }
        }

    fun showNotification(playPauseBtnDrawable: Int){
        this.playPauseBtnDrawable = playPauseBtnDrawable


        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

//        val intent = Intent(baseContext, PlayActivity :: class.java)
//        intent.putExtra("index", PlayerActivity.songPosition)
//        intent.putExtra( name: "class", value: "NowPlaying")
//        val contextIntent = PendingIntent.getActivity(this,0, intent, flags: 0)


        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        getBitmapAsyncAndDoWork("${BASE_URL +"data/img/playlist/"}${PlayActivity.songListArr[PlayActivity.position].albumId}.jpg")


    }

    private fun showNotification(){

        val intent = Intent(baseContext, HomeActivity::class.java)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val gson = Gson()

//        intent.putExtra("index", PlayActivity.position)
//        intent.putExtra("songList", gson.toJson(PlayActivity.songListArr))
//        intent.putExtra("position", PlayActivity.position)
//        intent.putExtra("ptype",PlayActivity.ptype)
//        intent.putExtra("onShuffle",PlayActivity.onShuffle)
//        intent.putExtra("class", "NowPlaying")

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayActivity.songListArr[PlayActivity.position].sname)
            .setContentText(PlayActivity.artistArrString)
            .setSmallIcon(R.drawable.spotify_logo_splash)
            .setLargeIcon(albumBitmap)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_back,"Previous", prevPendingIntent)
            .addAction(playPauseBtnDrawable,"Play", playPendingIntent)
            .addAction(R.drawable.ic_next,"Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon,"Exit", exitPendingIntent)
            .build()
        startForeground(8,notification)
    }

    private fun getBitmapAsyncAndDoWork(imageUrl: String) {
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    @Nullable transition: Transition<in Bitmap?>?
                ) {
                    albumBitmap = resource
                    showNotification()

                }
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }

    fun createMediaPlayer() {
        try {
            if (PlayActivity.mediaPlayerService!!.mediaPlayer == null) PlayActivity.mediaPlayerService!!.mediaPlayer =
                MediaPlayer()
            PlayActivity.mediaPlayerService!!.mediaPlayer!!.reset()
            PlayActivity.mediaPlayerService!!.mediaPlayer?.setDataSource("${BASE_URL + "data/music/"}${PlayActivity.songListArr[PlayActivity.position].sid}.mp3")
            PlayActivity.mediaPlayerService!!.mediaPlayer!!.prepare()
            PlayActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_pause)
            PlayActivity.mediaPlayerService!!.mediaPlayer?.setOnCompletionListener {
                playNextTrack()
            }

            PlayActivity.mediaPlayerService!!.showNotification(R.drawable.ic_pause)
        } catch (e: Exception) {
            return
        }
    }

    private fun playNextTrack() {
        if (PlayActivity.onShuffle){
            var newPos = Random.nextInt(PlayActivity.songListArr.size)
            while (newPos== PlayActivity.position){
                newPos = Random.nextInt(PlayActivity.songListArr.size)
            }
            PlayActivity.position =newPos
        }
        else{
            if (PlayActivity.position < PlayActivity.songListArr.size-1){
                PlayActivity.position +=1
            }
            else{
                PlayActivity.position =0
            }
        }
        Log.d("CHEEZY MUSIC POSITION","Index: ${PlayActivity.position}")
        PlayActivity.binding.startTimeText.text="00:00"
        PlayActivity.mediaPlayerService!!.createMediaPlayer()
        Glide.with(this)
            . load("${BASE_URL +"data/img/playlist/"}${PlayActivity.songListArr[PlayActivity.position].albumId}.jpg")
            .apply(RequestOptions().placeholder(R.drawable.playlist_default_img).centerCrop())
            .into(PlayActivity.binding.playMusicImage)

        PlayActivity.viewModel.viewModelScope.launch {
            PlayActivity.viewModel.addRecentSong(FavSongQuery(PlayActivity.curr_passHash,PlayActivity.songListArr[PlayActivity.position].sid.toString()))
        }
        PlayActivity.mediaPlayerService!!.showNotification(R.drawable.ic_pause)

        if (PlayActivity.songListArr[PlayActivity.position].isFav){
            PlayActivity.binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
        }
        else{
            PlayActivity.binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_unselected_white)
        }

        PlayActivity.binding.songNameTT.text = PlayActivity.songListArr[PlayActivity.position].sname
        PlayActivity.binding.songArtistsTT.text = PlayActivity.songListArr[PlayActivity.position].artist_name_arr.joinToString(", ")
        createMediaPlayer()
        PlayActivity.isPlaying = true
        PlayActivity.mediaPlayerService !!. mediaPlayer !!. start()
        PlayActivity.mediaPlayerService !!. showNotification(R.drawable.ic_pause)
        PlayActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_pause)



    }

}

