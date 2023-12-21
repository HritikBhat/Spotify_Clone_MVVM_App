package com.hritikbhat.spotify_mvvm_app.ui.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.setSongPosition
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.Utils.TransactionTypes
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper.BASE_URL
import com.hritikbhat.spotify_mvvm_app.utils.Services.MediaPlayerService
import com.hritikbhat.spotify_mvvm_app.viewModels.FavouritesViewModel
import com.hritikbhat.spotify_mvvm_app.databinding.ActivityPlayBinding
import com.hritikbhat.spotify_mvvm_app.ui.Activities.SongMoreOptionPlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayActivity : AppCompatActivity(),ServiceConnection {
    
    private lateinit var btnPlayPause: ImageView
    private var imgUrl = BASE_URL+"data/img/playlist/"
    private lateinit var intent: Intent
    private var sid: Int = 0
    private var albumId: Int = 0
    private lateinit var sname: String

    private lateinit var extras: Bundle

    private val initTimeFormatMMSS = "00:00"

    private lateinit var sharedPref:SharedPreferences


    companion object {
        var updateSeekBarJob:Job? = null
        var updateSeekBarJobDefault:Job?=null
        lateinit var songListArr: ArrayList<Song>
        var position: Int = 0
        var isPlaying:Boolean = true
        var ptype:Int = -1
        var isFav:Boolean = false
        lateinit var artistArrString: String
        var mediaPlayerService : MediaPlayerService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayBinding
        var onShuffle:Boolean = false
        @SuppressLint("StaticFieldLeak")
        lateinit var viewModel: FavouritesViewModel
        lateinit var currPassHash:String
    }

    override fun onResume() {
        super.onResume()
        try {
            if (isFav){
                binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
            }
            else{
                binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_unselected_white)
            }


            if (mediaPlayerService!=null){
                if (mediaPlayerService!!.mediaPlayer?.isPlaying!!){
                    btnPlayPause.setImageResource(R.drawable.ic_pause)
                    mediaPlayerService!!.showNotification(R.drawable.ic_pause)
                }
                else{
                        viewModel.viewModelScope.launch(Dispatchers.Main.immediate) {
                            updateSeekBarUI()
                        }

                    btnPlayPause.setImageResource(R.drawable.ic_play)
                    mediaPlayerService!!.showNotification(R.drawable.ic_play)
                }
            }

        }catch (_:Exception){
        }
    }

    private fun setLayout(){
        binding.seekBar.progress = 0
        btnPlayPause = binding.playPauseBtn
        currPassHash = sharedPref.getString("passHash", "").toString()
        sid = songListArr[position].sid
        isFav = songListArr[position].isFav
        albumId = songListArr[position].albumId
        sname = songListArr[position].sname
        artistArrString = songListArr[position].artist_name_arr.joinToString(", ")


        if (position==-1){
            position= intent.getIntExtra("position",-1)
        }

        if (ptype==1) binding.favouriteBtn.visibility=View.INVISIBLE else binding.favouriteBtn.visibility=View.VISIBLE



        if (isFav){
            binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
        }
        else{
            binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_unselected_white)
        }

        binding.songNameTT.text = sname

        binding.songArtistsTT.text = artistArrString

        Glide.with(this)
            .load("$imgUrl$albumId.jpg")
            .into(binding.playMusicImage)

        viewModel.viewModelScope.launch {
            addRecentSong(FavSongQuery(currPassHash,sid.toString()))
        }


        binding.favouriteBtn.setOnClickListener {
            if (isFav) {

                //DeleteFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(currPassHash, sid.toString()), TransactionTypes.DELETE_TRANSACTION)
                    binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_unselected_white)
                    isFav = false
                    
                }
            } else {

                //AddFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(currPassHash, sid.toString()), TransactionTypes.INSERT_TRANSACTION)
                    binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
                    isFav = true
                }
            }
            Log.d("Fav Flag gone to ","$isFav")
        }


        binding.nextBtn.setOnClickListener {
            Log.d("CHEEZY NOTIFICATION PLAY", "Length:${songListArr.size} Position: $position")
            setSongPosition(true)
            binding.startTimeText.text = initTimeFormatMMSS
            mediaPlayerService!!.mediaPlayer?.reset()
            setMusic()
        }

        binding.backBtn.setOnClickListener {
            Log.d("CHEEZY NOTIFICATION PLAY", "Length:${songListArr.size} Position: $position")
            setSongPosition(false)
            binding.startTimeText.text = initTimeFormatMMSS
            mediaPlayerService!!.mediaPlayer?.reset()
            setMusic()
        }

        binding.loopBtn.setOnClickListener {
            if (mediaPlayerService!!.mediaPlayer?.isLooping == true) {
                binding.loopBtn.setImageResource(R.drawable.ic_loop)
                mediaPlayerService!!.mediaPlayer?.isLooping = false
            } else {
                binding.loopBtn.setImageResource(R.drawable.ic_loop_green)
                mediaPlayerService!!.mediaPlayer?.isLooping = true
            }
        }

        btnPlayPause.setOnClickListener {
            if (mediaPlayerService!!.mediaPlayer?.isPlaying == true) {
                Log.d("MediaPlayerStatus","Stopped From PlayActivity")
                isPlaying=false
                mediaPlayerService!!.mediaPlayer?.pause()
                btnPlayPause.setImageResource(R.drawable.ic_play)
                mediaPlayerService!!.showNotification(R.drawable.ic_play)

            } else {
                Log.d("MediaPlayerStatus","Started From PlayActivity")
                isPlaying=true
                mediaPlayerService!!.mediaPlayer?.start()
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                mediaPlayerService!!.showNotification(R.drawable.ic_pause)
                updateSeekBar()


            }
            isPlaying = !isPlaying
        }

        binding.songMenuButton.setOnClickListener {
            onItemMoreOptionClick(albumId.toString(), songListArr, position, ptype)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
//                    if(binding.songMoreOptionLayout.visibility==View.VISIBLE){
//                        binding.playConstraintLayout.visibility=View.VISIBLE
//                        binding.songMoreOptionLayout.visibility = View.GONE
//
//                    } else{
//                        finish()
//                    }
                    finish()
                }
            }
        )

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayerService!!.mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    private fun setMusic(){
        setLayout()
        Log.d("MediaPlayerStatus","Started From PlayActivity")
        mediaPlayerService!!.createMediaPlayer()
        mediaPlayerService!!.mediaPlayer?.start()
        btnPlayPause.setImageResource(R.drawable.ic_pause)
        updateSeekBar()
    }


    private fun initializeLayout(){
        when(intent.getStringExtra("class")){
            "NowPlaying"->{

                setLayout()
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                updateSeekBar()
            }
            "ActivityOrAdapterPlaying"->{
                //For Starting Service
                val intent2 = Intent(this, MediaPlayerService::class.java)
                bindService(intent2, this, BIND_AUTO_CREATE)
                startService(intent2)

            }
        }
    }

    private suspend fun convertSongJsonStringToArraylist(jsonSongListString: String): ArrayList<Song> {
        return withContext(Dispatchers.Default){
            val gson = Gson()
            gson.fromJson(jsonSongListString, object : TypeToken<ArrayList<Song>>() {}.type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_play)

        viewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)

        sharedPref = SharedPreferenceInstance(this).getSPInstance()

        intent = getIntent()
        extras = intent.extras!!

        val jsonSongList = intent.getStringExtra("songList")

        viewModel.viewModelScope.launch {
            songListArr = convertSongJsonStringToArraylist(jsonSongList.toString())
        }


        onShuffle = intent.getBooleanExtra("onShuffle",false)
        position = intent.getIntExtra("position",-1)
        ptype = extras.getInt("ptype")


        initializeLayout()




    }

    private suspend fun addRecentSong(favSongQuery: FavSongQuery) {
        when (val operationResult: OperationResult<FavTransactionResp> = viewModel.addRecentSong(favSongQuery)) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    Log.d("CHEEZY_NOTIFICATION","Added to Recent")
                }
                else{
                    Log.e("CHEEZY_NOTIFICATION",favTransactionResp.message)
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

    private fun updateSeekBar() {
        if (updateSeekBarJobDefault!=null){
            updateSeekBarJob!!.cancel()
            updateSeekBarJobDefault!!.cancel()
        }
        updateSeekBarJobDefault = viewModel.viewModelScope.launch(Dispatchers.Default) {
            while (isActive){
                if (mediaPlayerService != null) {
                    if (mediaPlayerService!!.mediaPlayer != null) {
                        if (mediaPlayerService!!.mediaPlayer?.isPlaying == true) {
                            updateSeekBarUI()
                        }
                    }
                }
                delay(1000)

            }
        }
    }

    private fun updateSeekBarUI() {
        updateSeekBarJob = viewModel.viewModelScope.launch(Dispatchers.Main.immediate) {
            binding.seekBar.max = mediaPlayerService!!.mediaPlayer?.duration!!
            binding.seekBar.progress = mediaPlayerService!!.mediaPlayer?.currentPosition!!
            val currentTime = formatTime(mediaPlayerService!!.mediaPlayer?.currentPosition!!)
            val totalTime = formatTime(mediaPlayerService!!.mediaPlayer?.duration!!)
            binding.startTimeText.text = currentTime
            Log.d("CHEEZY_TIMING", mediaPlayerService!!.mediaPlayer?.currentPosition.toString())

            binding.endTimeText.text = totalTime
        }
    }

    private fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
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
                    Toast.makeText(applicationContext,favTransactionResp.message,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(applicationContext,favTransactionResp.message,Toast.LENGTH_LONG).show()
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

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MyBinder
        mediaPlayerService = binder.currentService()
        setMusic()
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        mediaPlayerService = null
    }


    private fun onItemMoreOptionClick(plid: String,items: MutableList<Song>, pos: Int, ptype: Int) {
        //Send To SongMore Option Activity
        val sendToSongMoreOptionIntent = Intent(this , SongMoreOptionPlayActivity::class.java)
        sendToSongMoreOptionIntent.putExtra("pos",pos)
        startActivity(sendToSongMoreOptionIntent)


    }




}