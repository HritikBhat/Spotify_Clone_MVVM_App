package com.hritikbhat.spotify_mvvm_app.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.GridItemAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.SearchAdapter
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.AllSearchItem
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentSearchBinding
import kotlinx.coroutines.launch

class SearchFragment : Fragment(), SearchAdapter.OnItemClickListener,PlaylistAdapter.OnItemClickListener,AddToPlaylistAdapter.OnItemClickListener {

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: FragmentSearchBinding

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0

    private val handler = Handler(Looper.myLooper()!!)
    private val searchRCAdapter = SearchAdapter()
    private val playlistAdapter = PlaylistAdapter()
    private val addToPlaylistAdapter = AddToPlaylistAdapter()
    private lateinit var context: Context

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    override fun onPause() {
        super.onPause()
        if (binding !=null){
            binding.searchEditText.setText("")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        context = binding.root.context

        sharedPref = context.getSharedPreferences(MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()



        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        // Set the ViewModel in the binding
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner


        binding.searchBtn.setOnClickListener(View.OnClickListener {
            //startSearchActivity(context)
            binding.searchFragmentStartLayout.visibility=View.GONE
            binding.searchLayout.visibility = View.VISIBLE
            binding.playlistRC.visibility = View.GONE

        })

        val genres = arrayListOf("Podcast", "New Releases", "Hindi", "Punjabi", "Tamil", "Telugu", "Pop", "Indie", "Trending",
            "Love", "Mood", "Party", "Devotional", "Hip-Hop", "Chill", "Gaming", "K-pop", "Rock", "Instrumental", "Country",
            "Classical")

        // Create an ArrayAdapter to populate the GridView
        val gridAdapter = GridItemAdapter(context, genres)

        binding.gridView.adapter = gridAdapter

        // Set up RecyclerView
        binding.playlistRC.layoutManager = LinearLayoutManager(context)
        binding.playlistRC.adapter = playlistAdapter

        //Set up AddToPlaylistRC
        binding.addToPlaylistRC.layoutManager = LinearLayoutManager(context)
        binding.addToPlaylistRC.adapter = addToPlaylistAdapter

        searchRCAdapter.setOnItemClickListener(this)
        playlistAdapter.setOnItemClickListener(this)
        addToPlaylistAdapter.setOnItemClickListener(this)
        binding.searchRc.layoutManager = LinearLayoutManager(context)
        binding.searchRc.adapter = searchRCAdapter

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.searchLayout.visibility==View.VISIBLE){
                        binding.searchFragmentStartLayout.visibility=View.VISIBLE
                        binding.searchLayout.visibility = View.GONE
                        binding.playlistRC.visibility = View.GONE
                        binding.searchEditText.setText("")
                        binding.addToPlaylistRC.visibility=View.GONE
                    }
                    else if(binding.playlistRC.visibility==View.VISIBLE){
                        binding.searchFragmentStartLayout.visibility=View.GONE
                        binding.searchLayout.visibility = View.VISIBLE
                        binding.playlistRC.visibility = View.GONE
                        binding.addToPlaylistRC.visibility=View.GONE
                    }
                    else if(binding.songMoreOptionLayout.visibility==View.VISIBLE){
                        binding.songMoreOptionLayout.visibility=View.GONE
                        binding.searchFragmentStartLayout.visibility=View.GONE
                        binding.searchLayout.visibility = View.GONE
                        binding.playlistRC.visibility = View.VISIBLE
                        binding.addToPlaylistRC.visibility=View.GONE
                    }
                    else if (binding.addToPlaylistRC.visibility==View.VISIBLE){
                        binding.songMoreOptionLayout.visibility=View.VISIBLE
                        binding.searchFragmentStartLayout.visibility=View.GONE
                        binding.searchLayout.visibility = View.GONE
                        binding.playlistRC.visibility = View.GONE
                        binding.addToPlaylistRC.visibility=View.GONE

                    }
                }
            }
        )



        binding.searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove any previously scheduled processing
                handler.removeCallbacksAndMessages(null)


                val searchQuery = binding.searchEditText.text.toString()

                if (searchQuery.isEmpty()){
                    binding.searchStartLayout.visibility = View.VISIBLE
                    binding.searchRc.visibility = View.GONE
                    binding.notFoundLayout.visibility = View.GONE
                    binding.songMoreOptionLayout.visibility=View.GONE
                    binding.addToPlaylistRC.visibility=View.GONE
                    binding.loadingLayout.visibility= View.GONE
                    return
                }
                else{
                    binding.searchStartLayout.visibility= View.GONE
                    binding.loadingLayout.visibility= View.VISIBLE
                }




                // Schedule data processing after 2 seconds
                handler.postDelayed({
                    viewModel.viewModelScope.launch {
                        getSearchResult(searchQuery)
                    }


                }, 1000) // 5000 milliseconds = 5 seconds
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        return binding.root
    }



    suspend  fun getSearchResult(searchQuery: String){
        val operationResult: OperationResult<List<AllSearchItem>> = viewModel.searchResult(curr_passHash,searchQuery)

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val searchList : List<AllSearchItem> = operationResult.data
                searchRCAdapter.updateItems(searchList)
                if (searchList.isEmpty()){
                    binding.searchQueryText.text = "'${binding.searchEditText.text.toString()}'"
                    binding.searchStartLayout.visibility = View.GONE
                    binding.searchRc.visibility = View.GONE
                    binding.notFoundLayout.visibility = View.VISIBLE
                    binding.songMoreOptionLayout.visibility=View.GONE
                    binding.addToPlaylistRC.visibility=View.GONE

                }
                else{
                    binding.searchStartLayout.visibility = View.GONE
                    binding.searchRc.visibility = View.VISIBLE
                    binding.notFoundLayout.visibility = View.GONE
                    binding.songMoreOptionLayout.visibility=View.GONE
                    binding.addToPlaylistRC.visibility=View.GONE
                    binding.loadingLayout.visibility= View.GONE
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
                Log.d("CHEEZY NOTIFICATION","API plid value: ${plq.plid}")
                playlistAdapter.setPlaylistItems(plq.plid,pname,aname,playListDetails.pltype, playListDetails.isFav,playListDetails)
                binding.searchFragmentStartLayout.visibility=View.GONE
                binding.searchLayout.visibility = View.GONE
                binding.playlistRC.visibility = View.VISIBLE
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

    override fun onItemClick(plid: Int, pname: String, aname: String, ptype: Int) {
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
        binding.searchFragmentStartLayout.visibility=View.GONE
        binding.searchLayout.visibility = View.GONE
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
        binding.searchFragmentStartLayout.visibility=View.GONE
        binding.searchLayout.visibility = View.GONE
        binding.playlistRC.visibility = View.GONE
        binding.songMoreOptionLayout.visibility=View.VISIBLE
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
                binding.searchStartLayout.visibility = View.GONE
                binding.searchRc.visibility = View.GONE
                binding.notFoundLayout.visibility = View.GONE
                binding.songMoreOptionLayout.visibility=View.GONE
                binding.addToPlaylistRC.visibility=View.GONE
                binding.playlistRC.visibility = View.VISIBLE
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

}
