package com.hritikbhat.spotify_mvvm_app.ui.Activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hritikbhat.spotify_mvvm_app.Adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.Models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.Models.OperationResult
import com.hritikbhat.spotify_mvvm_app.Models.Song
import com.hritikbhat.spotify_mvvm_app.Models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.Models.setSongPosition
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper.BASE_URL
import com.hritikbhat.spotify_mvvm_app.Utils.Services.MediaPlayerService
import com.hritikbhat.spotify_mvvm_app.ViewModels.FavouritesViewModel
import com.hritikbhat.spotify_mvvm_app.databinding.ActivityPlayBinding
import kotlinx.coroutines.launch
import kotlin.random.Random


class PlayActivity : AppCompatActivity(),ServiceConnection ,AddToPlaylistAdapter.OnItemClickListener{

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0


    private lateinit var btnPlayPause: ImageView
    private var audioUrl = BASE_URL+"data/music/"
    private var imgUrl = BASE_URL+"data/img/playlist/"
    private lateinit var intent: Intent
    private var sid: Int = 0
    private var albumId: Int = 0
    private lateinit var sname: String
    private val addToPlaylistAdapter = AddToPlaylistAdapter()

    private lateinit var extras: Bundle
    private var isFav:Boolean = false
    private lateinit var sharedPref: SharedPreferences


    companion object {
        lateinit var songListArr: ArrayList<Song>
        var position: Int = 0
        var isPlaying:Boolean = true
        var ptype:Int = -1
        lateinit var artistArrString: String
        var mediaPlayerService : MediaPlayerService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayBinding
        var onShuffle:Boolean = false
        @SuppressLint("StaticFieldLeak")
        lateinit var viewModel: FavouritesViewModel
        lateinit var curr_passHash:String
        val handler = Handler(Looper.getMainLooper())
    }



    private val MY_PREFS_NAME: String = "MY_PREFS"


    private fun setLayout(){
        binding.seekBar.progress = 0
        btnPlayPause = binding.playPauseBtn
        curr_passHash = sharedPref.getString("passHash", "").toString()
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
            addRecentSong(FavSongQuery(curr_passHash,sid.toString()))
        }


        binding.favouriteBtn.setOnClickListener(View.OnClickListener {
            if (isFav){

                //DeleteFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(curr_passHash,sid.toString()),DELETETRANSACTION)
                    binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_unselected_white)
                    isFav=false
                }
            }
            else{

                //AddFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(curr_passHash,sid.toString()),INSERTTRANSACTION)
                    binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
                    isFav=true
                }
            }
        })


        binding.nextBtn.setOnClickListener(View.OnClickListener {
            Log.d("CHEEZY NOTIFICATION PLAY","Length:${songListArr.size} Position: $position")
            setSongPosition(true)
            binding.startTimeText.text="00:00"
            mediaPlayerService!!.mediaPlayer?.reset()
            setMusic()
        })

        binding.backBtn.setOnClickListener(View.OnClickListener {
            Log.d("CHEEZY NOTIFICATION PLAY","Length:${songListArr.size} Position: $position")
            setSongPosition(false)
            binding.startTimeText.text="00:00"
            mediaPlayerService!!.mediaPlayer?.reset()
            setMusic()
        })

        binding.loopBtn.setOnClickListener(View.OnClickListener {
            if (mediaPlayerService!!.mediaPlayer?.isLooping == true){
                binding.loopBtn.setImageResource(R.drawable.ic_loop)
                mediaPlayerService!!.mediaPlayer?.isLooping=false
            }
            else{
                binding.loopBtn.setImageResource(R.drawable.ic_loop_green)
                mediaPlayerService!!.mediaPlayer?.isLooping=true
            }
        })

        btnPlayPause.setOnClickListener {
            if (mediaPlayerService!!.mediaPlayer?.isPlaying == true) {
                isPlaying=false
                mediaPlayerService!!.mediaPlayer?.pause()
                btnPlayPause.setImageResource(R.drawable.ic_play)
                mediaPlayerService!!.showNotification(R.drawable.ic_play)
            } else {
                isPlaying=true
                mediaPlayerService!!.mediaPlayer?.start()
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                mediaPlayerService!!.showNotification(R.drawable.ic_pause)
                updateSeekBar()

            }
            isPlaying = !isPlaying
        }

        binding.songMenuButton.setOnClickListener(View.OnClickListener {
            onItemMoreOptionClick(albumId.toString(), songListArr,position, ptype)
        })

        onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(binding.songMoreOptionLayout.visibility==View.VISIBLE){
                        binding.playConstraintLayout.visibility=View.VISIBLE
                        binding.songMoreOptionLayout.visibility = View.GONE

                    }
                    else{
                        finish()
                    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_play)

        viewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)

        sharedPref = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE)



        addToPlaylistAdapter.setOnItemClickListener(this)
        binding.addToPlaylistRC.layoutManager = LinearLayoutManager(this)
        binding.addToPlaylistRC.adapter = addToPlaylistAdapter


        intent = getIntent()
        extras = intent.extras!!

        val jsonSongList = intent.getStringExtra("songList")
        val gson = Gson()
        songListArr = gson.fromJson(jsonSongList, object : TypeToken<ArrayList<Song>>() {}.type)
        onShuffle = intent.getBooleanExtra("onShuffle",false)
        position = intent.getIntExtra("position",-1)
        ptype = extras.getInt("ptype")


        initializeLayout()




    }

    private suspend fun addRecentSong(favSongQuery: FavSongQuery) {
        var operationResult: OperationResult<FavTransactionResp> = viewModel.addRecentSong(favSongQuery)
        when (operationResult) {
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
        handler.postDelayed(object : Runnable {
            override fun run() {



                if (mediaPlayerService!=null){
                    if (mediaPlayerService!!.mediaPlayer!=null){
                        if(mediaPlayerService!!.mediaPlayer?.isPlaying == true) {
                            binding.seekBar.progress = mediaPlayerService!!.mediaPlayer?.currentPosition!!
                            val currentTime = formatTime(mediaPlayerService!!.mediaPlayer?.currentPosition!!)
                            val totalTime = formatTime(mediaPlayerService!!.mediaPlayer?.duration!!)
                            binding.startTimeText.text = currentTime
                            Log.d("CHEEZY_TIMING", mediaPlayerService!!.mediaPlayer?.currentPosition.toString())
                            binding.seekBar.max = mediaPlayerService!!.mediaPlayer?.duration!!
                            binding.endTimeText.text = totalTime

                            handler.postDelayed(this, 1000)
                        }
                    }

                }

            }
        }, 0)
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
        PlayActivity.mediaPlayerService!!.mediaPlayer?.reset()
        setMusic()



    }



    private fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        //mediaPlayerService!!.mediaPlayer?.release()
        //handler.removeCallbacksAndMessages(null)
    }

    suspend  fun setFavSongStatus( fQ: FavSongQuery,transType:Int){
        var operationResult: OperationResult<FavTransactionResp>
        if (transType==INSERTTRANSACTION){

            operationResult = viewModel.addFavSong(fQ)
        }
        else{
            operationResult= viewModel.removeFavSong(fQ)
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

    private suspend  fun setInitForAddToPlay(sid: String) {
        //binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
        binding.playConstraintLayout.visibility=View.GONE
        binding.songMoreOptionLayout.visibility=View.GONE
        binding.addToPlaylistRC.visibility=View.VISIBLE

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

    fun onItemMoreOptionClick(plid: String,items: MutableList<Song>, pos: Int, ptype: Int) {
        binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
        binding.playConstraintLayout.visibility=View.GONE
        binding.songMoreOptionLayout.visibility=View.VISIBLE
        binding.addToPlaylistRC.visibility=View.GONE
        val isFavSong = items[pos].isFav

        Log.d("CHEEZY NOTIFICATION","isFavSong Flag: $isFavSong")


        binding.songName.text = items[pos].sname
        val plURL = RetrofitHelper.BASE_URL +"data/img/playlist/${items[pos].albumId}.jpg"

        Glide.with(binding.root.context)
            .load(plURL).thumbnail()
            .into(binding.songAlbumImg)

        binding.songArtists.text=items[pos].artist_name_arr.joinToString(", ")

        if (isFavSong){
            binding.songOptionImg.setImageResource(R.drawable.ic_fav_selected_white)
        }

        binding.addToPlaylistSong.setOnClickListener(View.OnClickListener {
            val sid = items[pos].sid
            viewModel.viewModelScope.launch {
                setInitForAddToPlay(sid.toString())
            }

        })

        binding.likeSong.setOnClickListener(View.OnClickListener {


            if (isFavSong){

                //DeleteFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(
                        FavSongQuery(curr_passHash, items[pos].sid.toString()),
                        DELETETRANSACTION
                    )
                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
                    //playlistAdapter.setSongFavStatus(pos,false)
                    binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
                }
            }
            else{

                //AddFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(curr_passHash,items[pos].sid.toString()),INSERTTRANSACTION)
                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_selected_white)
                    //playlistAdapter.setSongFavStatus(pos,true)
                    binding.favouriteBtn.setBackgroundResource(R.drawable.ic_fav_unselected_white)
                }
            }
        })
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
                    Toast.makeText(this,favTransactionResp.message,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this,favTransactionResp.message,Toast.LENGTH_LONG).show()
                }

                binding.playConstraintLayout.visibility = View.VISIBLE
                binding.songMoreOptionLayout.visibility=View.GONE
                binding.addToPlaylistRC.visibility=View.GONE
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