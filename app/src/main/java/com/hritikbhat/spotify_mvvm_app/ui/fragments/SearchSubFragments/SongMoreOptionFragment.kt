package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.Utils.TransactionTypes
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentSongMoreOptionBinding
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import kotlinx.coroutines.launch

class SongMoreOptionFragment : Fragment() {
    

    private lateinit var binding: FragmentSongMoreOptionBinding
    private lateinit var currPassHash:String
    private lateinit var sharedPref: SharedPreferences

    private lateinit var viewModel: SearchViewModel
    private lateinit var viewModel2: FavPlaylistViewModel

    private lateinit var songData : Song

    private lateinit var plid:String
    private var ptype:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_more_option, container, false)

        val songsMoreOptionFragmentArgs = SongMoreOptionFragmentArgs.fromBundle(requireArguments())


        songData = songsMoreOptionFragmentArgs.songData

        plid = songsMoreOptionFragmentArgs.plid
        ptype = songsMoreOptionFragmentArgs.ptype


        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        viewModel2 = ViewModelProvider(this)[FavPlaylistViewModel::class.java]


        binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
//        binding.searchFragmentStartLayout.visibility=View.GONE
//        binding.searchLayout.visibility = View.GONE
//        binding.playlistRC.visibility = View.GONE
//        binding.songMoreOptionLayout.visibility=View.VISIBLE
        val isFavSong = songData.isFav

        Log.d("CHEEZY NOTIFICATION","isFavSong Flag: $isFavSong")


        binding.songName.text = songData.sname
        val plURL = RetrofitHelper.BASE_URL +"data/img/playlist/${songData.albumId}.jpg"

        Glide.with(binding.root.context)
            .load(plURL).thumbnail()
            .into(binding.songAlbumImg)

        binding.songArtists.text=songData.artist_name_arr.joinToString(", ")

        if (isFavSong){
            binding.songOptionImg.setImageResource(R.drawable.ic_fav_selected_white)
        }

        binding.addToPlaylistSong.setOnClickListener{
            val sid = songData.sid
//            viewModel.viewModelScope.launch {
//                setInitForAddToPlay(sid.toString())
//            }
            //Send it to AddToPlay Frag
            val action = SongMoreOptionFragmentDirections.actionSongMoreOptionFragmentToAddToCustomPlaylistFragment()
            action.sid = sid
            findNavController().navigate(action)
        }

        //need ptype and plid sent here
        if (ptype==3){
            binding.removeSongFromPlaylist.visibility=View.VISIBLE
            binding.removeSongFromPlaylist.setOnClickListener{
                val sid = songData.sid
                viewModel.viewModelScope.launch {
                    removeSongFromPlaylist(currPassHash,plid,sid.toString())
                }
            }
        }else{binding.removeSongFromPlaylist.visibility=View.GONE}

        binding.likeSong.setOnClickListener{
            if (isFavSong){

                //DeleteFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(currPassHash,songData.sid.toString()),
                        TransactionTypes.DELETE_TRANSACTION
                    )
                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
                    //Send back updated items back when clicked back
                }
            }
            else{

                //AddFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(currPassHash,songData.sid.toString()),
                        TransactionTypes.INSERT_TRANSACTION
                    )
                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_selected_white)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(ContentValues.TAG, "Fragment back pressed invoked")
                    // Do custom work here

                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        findNavController().popBackStack()
                    }
                }
            }
        )

        binding.songMoreOptionBackBtn.setOnClickListener{
            findNavController().popBackStack()
        }


        return binding.root
    }

    suspend  fun setFavSongStatus(fQ: FavSongQuery, transType:TransactionTypes){
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
                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()
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

    private suspend fun removeSongFromPlaylist(currPassHash: String, plid: String, sid: String) {
        val operationResult: OperationResult<FavTransactionResp> = viewModel2.deleteSongFromPlaylist(
            AddSongPlaylistQuery(currPassHash,plid,sid)
        )

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result


                val favTransactionResp : FavTransactionResp = operationResult.data


                if (favTransactionResp.success){
//                    binding.playlistRC.visibility = View.VISIBLE
//                    binding.noFavouriteLayout.visibility = View.GONE
//                    binding.songMoreOptionLayout.visibility=View.GONE
//                    binding.addToPlaylistRC.visibility=View.GONE
//                    binding.favPlaylistRC.visibility = View.GONE
//                    getPlaylistDetails(plid.toInt(),plqPname,plqAname,plqPType,PlayListQuery(plid,currPassHash))

                    //BackTo Show

                    Toast.makeText(context,favTransactionResp.message,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,favTransactionResp.message,Toast.LENGTH_LONG).show()
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