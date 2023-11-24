package com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments

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
import com.hritikbhat.spotify_mvvm_app.adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.FavPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.CustomPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.viewModels.subFragmentsViewModels.FavPlaylistViewModel
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistsX
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import kotlinx.coroutines.launch

class FavPlaylistFragment : Fragment(), FavPlaylistAdapter.OnItemClickListener,PlaylistAdapter.OnItemClickListener,AddToPlaylistAdapter.OnItemClickListener {
    private lateinit var viewModel: FavPlaylistViewModel
    private lateinit var binding: FragmentFavPlaylistBinding
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

    private var plqAname = ""
    private var plqPname = ""
    private var plqPType = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_playlist, container, false)
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

        viewModel = ViewModelProvider(this).get(FavPlaylistViewModel::class.java)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(binding.playlistRC.visibility==View.VISIBLE){
                        viewModel.viewModelScope.launch {
                            getUserFavPlaylist()
                        }
                        if (favPlaylistRCAdapter.itemCount==0){
                            binding.noFavouriteLayout.visibility=View.VISIBLE
                            binding.favPlaylistRC.visibility = View.GONE
                        }else{
                            binding.noFavouriteLayout.visibility=View.GONE
                            binding.favPlaylistRC.visibility = View.VISIBLE
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
        binding.favPlaylistRC.layoutManager = LinearLayoutManager(context)
        binding.favPlaylistRC.adapter = favPlaylistRCAdapter

        binding.playlistRC.layoutManager = LinearLayoutManager(context)
        binding.playlistRC.adapter = playlistAdapter

        binding.addToPlaylistRC.layoutManager = LinearLayoutManager(context)
        binding.addToPlaylistRC.adapter = addToPlaylistAdapter

        binding.customPlaylistNextBtn.setOnClickListener(View.OnClickListener {
            viewModel.viewModelScope.launch {
                createCustomPlaylist()
            }
        })

        binding.customPlaylistCancelBtn.setOnClickListener(View.OnClickListener {
            binding.customPlaylistLayout.visibility=View.GONE
            binding.noFavouriteLayout.visibility=View.GONE
            binding.favPlaylistRC.visibility = View.VISIBLE
            binding2.tabLayout.visibility=View.VISIBLE
            binding2.viewPager.isUserInputEnabled = true

            binding.customPlaylistNameTT.setText("")
        })


        return binding.root

    }

    private suspend fun createCustomPlaylist(){
        val customName = binding.customPlaylistNameTT.text.toString()
        var operationResult: OperationResult<FavTransactionResp> = viewModel.addCustomPlaylist(CustomPlaylistQuery(customName,curr_passHash))
        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){

                    getUserFavPlaylist()


                    binding.customPlaylistLayout.visibility=View.GONE
                    binding.noFavouriteLayout.visibility=View.GONE
                    binding.favPlaylistRC.visibility = View.VISIBLE
                    binding2.tabLayout.visibility=View.VISIBLE
                    binding2.viewPager.isUserInputEnabled = true

                    binding.customPlaylistNameTT.setText("")

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

    override fun onItemClick(plid: Int, pname: String, ptype: Int, aname: String) {
        viewModel.viewModelScope.launch {
            if (plid==-2){
                binding.customPlaylistLayout.visibility=View.VISIBLE
                binding.noFavouriteLayout.visibility=View.GONE
                binding.favPlaylistRC.visibility = View.GONE
                binding2.tabLayout.visibility=View.GONE
                binding2.viewPager.isUserInputEnabled = false

            }
            else{
                plqPname = pname
                plqAname = aname
                plqPType = ptype
                getPlaylistDetails(plid,pname,aname,ptype,PlayListQuery(plid.toString(),curr_passHash))
            }

        }
    }

    override fun onItemClick() {
        try {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }catch (e:Exception){
            e.printStackTrace()
        }
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

    private suspend  fun setInitForAddToPlay(sid: String) {
        //binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
        binding.favPlaylistRC.visibility=View.GONE
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

    override fun onItemMoreOptionClick(plid:String,items: MutableList<Song>, pos: Int, ptype: Int) {
        binding.removeSongFromPlaylist.visibility=View.GONE
        binding.songOptionImg.setImageResource(R.drawable.ic_fav_unselected_white)
        binding.favPlaylistRC.visibility=View.GONE
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

        if (ptype==3){
            binding.removeSongFromPlaylist.visibility=View.VISIBLE
            binding.removeSongFromPlaylist.setOnClickListener(View.OnClickListener {
                val sid = items[pos].sid
                viewModel.viewModelScope.launch {
                    removeSongFromPlaylist(curr_passHash,plid,sid.toString())
                }
            })
        }



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

    override fun onItemPlaylistMoreOptionClick(plid:String,pName:String, ptype: Int) {
        binding.favPlaylistRC.visibility=View.GONE
        binding.noFavouriteLayout.visibility = View.GONE
        binding.playlistRC.visibility = View.GONE
        binding.customPlaylistMoreOptionLayout.visibility=View.VISIBLE
        binding.addToPlaylistRC.visibility=View.GONE

        binding.playlistName.text = pName

        binding.deletePlaylist.setOnClickListener(View.OnClickListener {
            viewModel.viewModelScope.launch {
                setInitForDeleteCustPlay(plid)
            }

        })

    }


    private suspend fun removeSongFromPlaylist(curr_passHash: String, plid: String, sid: String) {
        var operationResult: OperationResult<FavTransactionResp> = viewModel.deleteSongFromPlaylist(
            AddSongPlaylistQuery(curr_passHash,plid,sid)
        )

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result


                val favTransactionResp : FavTransactionResp = operationResult.data


                if (favTransactionResp.success){
                    binding.playlistRC.visibility = View.VISIBLE
                    binding.noFavouriteLayout.visibility = View.GONE
                    binding.songMoreOptionLayout.visibility=View.GONE
                    binding.addToPlaylistRC.visibility=View.GONE
                    binding.favPlaylistRC.visibility = View.GONE
                    getPlaylistDetails(plid.toInt(),plqPname,plqAname,plqPType,PlayListQuery(plid,curr_passHash))

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


    private suspend fun setInitForDeleteCustPlay(plid: String) {

        var operationResult: OperationResult<FavTransactionResp> = viewModel.removeCustomPlayList(
            AddSongPlaylistQuery(curr_passHash,plid,"")
        )

        when (operationResult) {
            is OperationResult.Success -> {



                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    getUserFavPlaylist()

                    binding.favPlaylistRC.visibility=View.VISIBLE
                    binding.noFavouriteLayout.visibility = View.GONE
                    binding.playlistRC.visibility = View.GONE
                    binding.songMoreOptionLayout.visibility=View.GONE
                    binding.customPlaylistMoreOptionLayout.visibility=View.GONE
                    binding2.tabLayout.visibility=View.VISIBLE
                    binding2.viewPager.isUserInputEnabled = true
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
                binding.favPlaylistRC.visibility = View.GONE
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


    suspend  fun getPlaylistDetails(plid:Int,pname: String, aname: String,ptype:Int, plq: PlayListQuery){
        val operationResult: OperationResult<PlayListDetail> = if(plid==-1){
            //Fav Songs Section
            viewModel.getFavSongsDetails(plq)
        } else{
            viewModel.getPlaylistDetails(plq)
        }


        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result


                val playListDetails : PlayListDetail = operationResult.data
                if (playListDetails.songs==null || playListDetails.songs.isEmpty()){
                    var message:String=""
                    if(plid==-1){
                        message = "No Favourite Songs Found"
                    }
                    else{
                        message = "No Songs Found"
                        binding.noFavouriteLayout.visibility=View.GONE
                        binding.favPlaylistRC.visibility = View.VISIBLE
                        binding.playlistRC.visibility = View.GONE
                        binding2.tabLayout.visibility = View.VISIBLE
                        binding2.viewPager.isUserInputEnabled = true
                    }
                    Toast.makeText(binding.root.context,message,Toast.LENGTH_LONG).show()
                }
                else{
                    playlistAdapter.setPlaylistItems(
                        plq.plid,
                        pname,
                        aname,
                        ptype,
                        playListDetails.isFav,
                        playListDetails
                    )
                    binding.noFavouriteLayout.visibility=View.GONE
                    binding.favPlaylistRC.visibility = View.GONE
                    binding.playlistRC.visibility = View.VISIBLE
                    binding2.tabLayout.visibility = View.GONE
                    binding2.viewPager.isUserInputEnabled = false
                    // Process searchList here
                }

            }
            is OperationResult.Error -> {
                // An error occurred, handle the error
                val errorMessage = operationResult.message
                Log.e("ERROR", errorMessage)
                // Handle the error, for example, display an error message to the user
            }
        }
    }



    private suspend  fun getUserFavPlaylist(){
        var operationResult: OperationResult<favPlaylists> =
            viewModel.getUserFavPlaylist(FavPlaylistQuery(curr_passHash,"-1"))

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : favPlaylists = operationResult.data

//                if (favTransactionResp.favPlaylists.isEmpty()){
//                    binding.favPlaylistRC.visibility=View.GONE
//                    binding.noFavouriteLayout.visibility = View.VISIBLE
//                }
//                else{
//                    binding.favPlaylistRC.visibility=View.VISIBLE
//                    binding.noFavouriteLayout.visibility = View.GONE
//                }

                val favplaylist = ArrayList<FavPlaylistsX>()
                favplaylist.add(FavPlaylistsX(-2, "Create Playlist",-1,""))
                favplaylist.add(FavPlaylistsX(-1,"Favourite Songs",-1,""))
                favplaylist.addAll(favTransactionResp.favPlaylists)

                favPlaylistRCAdapter.updateItems(favplaylist.toList())

                binding.loadingLayout.visibility= View.GONE
                binding.favPlaylistRC.visibility= View.VISIBLE

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