package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentDoSearchBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentShowPlaylistSongsBinding
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import kotlinx.coroutines.launch

class ShowPlaylistSongsFragment : Fragment(),PlaylistAdapter.OnItemClickListener {

    private lateinit var binding: FragmentShowPlaylistSongsBinding

    private val playlistAdapter = PlaylistAdapter()
    private lateinit var sharedPref: SharedPreferences
    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0

    private lateinit var viewModel: SearchViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_playlist_songs, container, false)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        sharedPref = requireContext().getSharedPreferences(MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        playlistAdapter.setOnItemClickListener(this)
        // Set up RecyclerView
        binding.playlistRC.layoutManager = LinearLayoutManager(context)
        binding.playlistRC.adapter = playlistAdapter





        return binding.root
    }

    suspend  fun getPlaylistDetails(pname: String, aname: String, ptype: Int, plq: PlayListQuery){
        val operationResult: OperationResult<PlayListDetail> = viewModel.getPlaylistDetails(plq)

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val playListDetails : PlayListDetail = operationResult.data
                Log.d("CHEEZY NOTIFICATION","API plid value: ${plq.plid}")
                playlistAdapter.setPlaylistItems(plq.plid,pname,aname,playListDetails.pltype, playListDetails.isFav,playListDetails)
//                binding.searchFragmentStartLayout.visibility=View.GONE
//                binding.searchLayout.visibility = View.GONE
//                binding.playlistRC.visibility = View.VISIBLE
//                binding.addToPlaylistRC.visibility=View.GONE
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

    override fun onItemClick() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun onFavPlaylistButtonClick(isFav: Boolean,plid:Int) {
        if (isFav){

            //DeleteFavSong
            viewModel.viewModelScope.launch {
                setFavPlaylistStatus(FavPlaylistQuery(curr_passHash,plid.toString()),DELETETRANSACTION)
            }
        }
        else{

            //AddFavSong
            viewModel.viewModelScope.launch {
                setFavPlaylistStatus(FavPlaylistQuery(curr_passHash,plid.toString()),INSERTTRANSACTION)
            }
        }
    }

    private suspend  fun setFavPlaylistStatus(fQ: FavPlaylistQuery, transType:Int){
        var operationResult: OperationResult<FavTransactionResp>
        if (transType==INSERTTRANSACTION){

            operationResult = viewModel.addFavPlaylist(fQ)
        }
        else{
            operationResult= viewModel.removeFavPlaylist(fQ)
        }

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,"Something went wrong!", Toast.LENGTH_LONG).show()
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

    override fun onItemMoreOptionClick(plid: String,items: MutableList<Song>, pos: Int, ptype: Int) {
       //Send to SOngMoreOption
    }

    // In Progress
    override fun onItemPlaylistMoreOptionClick(plid: String, pname: String, ptype: Int) {
        //Send to PlaylistMoreOption
    }


}