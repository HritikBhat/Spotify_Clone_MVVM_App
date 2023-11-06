package com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouriteSubFragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.Adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.Adapters.FavPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.Adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.Models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.ViewModels.SubFragmentsViewModels.FavAlbumViewModel
import com.hritikbhat.spotify_mvvm_app.Models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.Models.OperationResult
import com.hritikbhat.spotify_mvvm_app.Models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.Models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.Models.Song
import com.hritikbhat.spotify_mvvm_app.Models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavAlbumBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouritesFragment
import kotlinx.coroutines.launch

class FavAlbumFragment : Fragment(), FavPlaylistAdapter.OnItemClickListener,PlaylistAdapter.OnItemClickListener,AddToPlaylistAdapter.OnItemClickListener {
    private lateinit var viewModel: FavAlbumViewModel
    private lateinit var binding: FragmentFavAlbumBinding
    private lateinit var binding2: FragmentFavouritesBinding

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0

    private val handler = Handler(Looper.myLooper()!!)
    private lateinit var context: Context

    private val favPlaylistRCAdapter = FavPlaylistAdapter()
    private val playlistAdapter = PlaylistAdapter()
    private val addToPlaylistAdapter = AddToPlaylistAdapter()

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_album, container, false)
        binding2 = (requireParentFragment() as FavouritesFragment).binding
        context = binding.root.context

        sharedPref = context.getSharedPreferences(
            MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        favPlaylistRCAdapter.setOnItemClickListener(this)
        playlistAdapter.setOnItemClickListener(this)
        addToPlaylistAdapter.setOnItemClickListener(this)

        viewModel = ViewModelProvider(this).get(FavAlbumViewModel::class.java)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(binding.playlistRC.visibility==View.VISIBLE){
                        viewModel.viewModelScope.launch {
                            getUserFavPlaylist()
                        }
                        if (favPlaylistRCAdapter.itemCount==0){
                            binding.noFavouriteLayout.visibility=View.VISIBLE
                            binding.favAlbumRC.visibility = View.GONE
                        }else{
                            binding.noFavouriteLayout.visibility=View.GONE
                            binding.favAlbumRC.visibility = View.VISIBLE
                        }
                        binding2.tabLayout.visibility = View.VISIBLE
                        binding2.viewPager.isUserInputEnabled = true
                        binding.playlistRC.visibility = View.GONE
                    }
                }
            }
        )



        viewModel.viewModelScope.launch {
            getUserFavPlaylist()
        }

        // Set up RecyclerView
        binding.favAlbumRC.layoutManager = LinearLayoutManager(context)
        binding.favAlbumRC.adapter = favPlaylistRCAdapter

        binding.playlistRC.layoutManager = LinearLayoutManager(context)
        binding.playlistRC.adapter = playlistAdapter

        binding.addToPlaylistRC.layoutManager = LinearLayoutManager(context)
        binding.addToPlaylistRC.adapter = addToPlaylistAdapter


        return binding.root

    }

    override fun onItemClick(plid: Int, pname: String, ptype: Int, aname: String) {
        viewModel.viewModelScope.launch {
            getPlaylistDetails(pname,aname,ptype,PlayListQuery(plid.toString(),curr_passHash))
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

    private suspend  fun setInitForAddToPlay(sid: String) {
        //binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
        binding.favAlbumRC.visibility=View.GONE
        binding.noFavouriteLayout.visibility = View.GONE
        binding.playlistRC.visibility = View.GONE
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

    override fun onItemMoreOptionClick(plid: String,items: MutableList<Song>, pos: Int, ptype: Int) {
        binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
        binding.favAlbumRC.visibility=View.GONE
        binding.noFavouriteLayout.visibility = View.GONE
        binding.playlistRC.visibility = View.GONE
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
                    setFavSongStatus(FavSongQuery(curr_passHash,items[pos].sid.toString()),DELETETRANSACTION)
                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
                    playlistAdapter.setSongFavStatus(pos,false)
                }
            }
            else{

                //AddFavSong
                viewModel.viewModelScope.launch {
                    setFavSongStatus(FavSongQuery(curr_passHash,items[pos].sid.toString()),INSERTTRANSACTION)
                    binding.songOptionImg.setImageResource(R.drawable.ic_fav_selected_white)
                    playlistAdapter.setSongFavStatus(pos,true)
                }
            }
        })
    }

    override fun onItemPlaylistMoreOptionClick(plid: String, pname: String, ptype: Int) {
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

    suspend  fun getPlaylistDetails(pname: String, aname: String, ptype: Int, plq: PlayListQuery){
        val operationResult: OperationResult<PlayListDetail> = viewModel.getPlaylistDetails(plq)

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val playListDetails : PlayListDetail = operationResult.data
                playlistAdapter.setPlaylistItems(
                    plq.plid,
                    pname,
                    aname,
                    ptype,
                    playListDetails.isFav,
                    playListDetails
                )
                binding.noFavouriteLayout.visibility=View.GONE
                binding.favAlbumRC.visibility = View.GONE
                binding.playlistRC.visibility = View.VISIBLE
                binding2.tabLayout.visibility = View.GONE
                binding2.viewPager.isUserInputEnabled = false
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

    private suspend fun addSongToCustomPlaylist(curr_passHash: String, plid: String, sid: String) {
        var operationResult: OperationResult<FavTransactionResp> = viewModel.addSongToPlaylist(
            AddSongPlaylistQuery(curr_passHash,plid,sid)
        )

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    Toast.makeText(context,favTransactionResp.message,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,favTransactionResp.message,Toast.LENGTH_LONG).show()
                }

                binding.playlistRC.visibility = View.VISIBLE
                binding.noFavouriteLayout.visibility = View.GONE
                binding.songMoreOptionLayout.visibility=View.GONE
                binding.addToPlaylistRC.visibility=View.GONE
                binding.favAlbumRC.visibility = View.GONE
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

    override fun onSelectingAddToPlaylistItemClick(plid: String, sid: String) {
        //AddFavSong
        viewModel.viewModelScope.launch {
            addSongToCustomPlaylist(curr_passHash,plid,sid)
        }
    }



    private suspend  fun getUserFavPlaylist(){
        var operationResult: OperationResult<favPlaylists> =
            viewModel.getUserFavAlbum(FavPlaylistQuery(curr_passHash,"-1"))

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : favPlaylists = operationResult.data

                if (favTransactionResp.favPlaylists.isEmpty()){
                    binding.favAlbumRC.visibility=View.GONE
                    binding.noFavouriteLayout.visibility = View.VISIBLE
                }
                else{
                    binding.favAlbumRC.visibility=View.VISIBLE
                    binding.noFavouriteLayout.visibility = View.GONE
                }
                favPlaylistRCAdapter.updateItems(favTransactionResp.favPlaylists)

                binding.loadingLayout.visibility= View.GONE
                binding.favAlbumRC.visibility= View.VISIBLE

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