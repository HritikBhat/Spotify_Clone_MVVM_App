package com.hritikbhat.spotify_mvvm_app.utils.Services

// MediaPlayerService.kt

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper.BASE_URL
import com.hritikbhat.spotify_mvvm_app.ui.activities.HomeActivity
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
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
            .setOngoing(true)
            .addAction(R.drawable.ic_back,"Previous", prevPendingIntent)
            .addAction(playPauseBtnDrawable,"Play", playPendingIntent)
            .addAction(R.drawable.ic_next,"Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon,"Exit", exitPendingIntent)
            .build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(8,notification)
        } else {
            //For API 34 and above
            startForeground(8, notification,
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        }

    }

    private fun getBitmapAsyncAndDoWork(imageUrl: String) {
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    albumBitmap = resource
                    showNotification()

                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun createMediaPlayer() {
        try {
            if (PlayActivity.mediaPlayerService!!.mediaPlayer == null) PlayActivity.mediaPlayerService!!.mediaPlayer =
                MediaPlayer()
            PlayActivity.mediaPlayerService!!.mediaPlayer!!.reset()
            PlayActivity.mediaPlayerService!!.mediaPlayer?.setDataSource("${BASE_URL + "data/music/"}${PlayActivity.songListArr[PlayActivity.position].sid}.mp3")
            PlayActivity.mediaPlayerService!!.mediaPlayer!!.prepare()
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
        PlayActivity.mediaPlayerService!!.createMediaPlayer()
        PlayActivity.mediaPlayerService!!.showNotification(R.drawable.ic_pause)
        PlayActivity.isPlaying = true
        PlayActivity.mediaPlayerService !!. mediaPlayer !!. start()
        PlayActivity.mediaPlayerService !!. showNotification(R.drawable.ic_pause)
    }

}

