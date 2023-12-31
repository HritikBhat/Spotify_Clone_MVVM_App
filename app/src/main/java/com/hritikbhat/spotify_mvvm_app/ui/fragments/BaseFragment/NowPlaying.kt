package com.hritikbhat.spotify_mvvm_app.ui.Fragments.BaseFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.hritikbhat.spotify_mvvm_app.models.setSongPosition
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentPlayingNowBinding
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity

class NowPlaying : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentPlayingNowBinding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //requireContext().theme.applyStyle(HomeActivity.currentTheme[HomeActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_playing_now, container, false)
        binding = FragmentPlayingNowBinding.bind(view)
        binding.root.visibility = View.GONE
        binding.nowPlayingPlayBtn.setOnClickListener {
            if(PlayActivity.mediaPlayerService?.mediaPlayer!!.isPlaying) pauseMusic() else playMusic()
        }
        binding.nowPlayingNextBtn.setOnClickListener {
            setSongPosition(increment = true)
            PlayActivity.mediaPlayerService!!.createMediaPlayer()
            binding.nowPlayingSongName.text = PlayActivity.songListArr[PlayActivity.position].sname
            playMusic()
        }



        binding.root.setOnClickListener {
            val songListArr = PlayActivity.songListArr
            Log.d("Fav Flag gone to ","${PlayActivity.isFav}")
            Log.d("Song List Before is : ",PlayActivity.songListArr.toString())
            songListArr[PlayActivity.position].isFav = PlayActivity.isFav
            Log.d("Song List After is : ",PlayActivity.songListArr.toString())

            val intent = Intent(requireContext(), PlayActivity::class.java)

            val gson = Gson()
            intent.putExtra("index", PlayActivity.position)
            intent.putExtra("songList", gson.toJson(songListArr))
            intent.putExtra("position", PlayActivity.position)
            intent.putExtra("ptype",PlayActivity.ptype)
            intent.putExtra("onShuffle",PlayActivity.onShuffle)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayActivity.mediaPlayerService != null){
            if (PlayActivity.mediaPlayerService!!.mediaPlayer!=null){
                binding.root.visibility = View.VISIBLE
//              binding.root.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                binding.nowPlayingSongName.isSelected = true
                binding.nowPlayingSongName.text = PlayActivity.songListArr[PlayActivity.position].sname
                if(PlayActivity.mediaPlayerService!!.mediaPlayer!!.isPlaying) binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_pause)
                else binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_play)
            }

        }
    }

    private fun playMusic(){
        Log.d("MediaPlayerStatus","Started From NowPlaying")
        PlayActivity.isPlaying = true
        PlayActivity.mediaPlayerService!!.mediaPlayer!!.start()
        binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_pause)
        PlayActivity.mediaPlayerService!!.showNotification(R.drawable.ic_pause)
    }
    private fun pauseMusic(){
        Log.d("MediaPlayerStatus","Stopped From NowPlaying")
        PlayActivity.isPlaying = false
        PlayActivity.mediaPlayerService!!.mediaPlayer!!.pause()
        binding.nowPlayingPlayBtn.setImageResource(R.drawable.ic_play)
        PlayActivity.mediaPlayerService!!.showNotification(R.drawable.ic_play)
    }
}