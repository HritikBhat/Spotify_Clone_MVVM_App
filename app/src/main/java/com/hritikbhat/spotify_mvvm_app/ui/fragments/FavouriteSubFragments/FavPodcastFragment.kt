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
import com.hritikbhat.spotify_mvvm_app.adapters.FavPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.viewModels.subFragmentsViewModels.FavPodcastViewModel
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavPodcastBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import kotlinx.coroutines.launch

class FavPodcastFragment : Fragment(), FavPlaylistAdapter.OnItemClickListener,PlaylistAdapter.OnItemClickListener {
    private lateinit var viewModel: FavPodcastViewModel
    private lateinit var binding: FragmentFavPodcastBinding
    private lateinit var binding2: FragmentFavouritesBinding

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0

    private val handler = Handler(Looper.myLooper()!!)
    private lateinit var context: Context

    private val favPlaylistRCAdapter = FavPlaylistAdapter()
    private val playlistAdapter = PlaylistAdapter()

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_podcast, container, false)
        binding2 = (requireParentFragment() as FavouritesFragment).binding
        context = binding.root.context

        sharedPref = context.getSharedPreferences(
            MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        favPlaylistRCAdapter.setOnItemClickListener(this)
        playlistAdapter.setOnItemClickListener(this)

        viewModel = ViewModelProvider(this).get(FavPodcastViewModel::class.java)

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
                            binding.favPodcastRC.visibility = View.GONE
                        }else{
                            binding.noFavouriteLayout.visibility=View.GONE
                            binding.favPodcastRC.visibility = View.VISIBLE
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
        binding.favPodcastRC.layoutManager = LinearLayoutManager(context)
        binding.favPodcastRC.adapter = favPlaylistRCAdapter

        binding.playlistRC.layoutManager = LinearLayoutManager(context)
        binding.playlistRC.adapter = playlistAdapter


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

    override fun onItemMoreOptionClick(plid: String,items: MutableList<Song>, i: Int, ptype: Int) {

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

    suspend  fun getPlaylistDetails(pname: String, aname: String,ptype:Int, plq: PlayListQuery){
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
                binding.favPodcastRC.visibility = View.GONE
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





    private suspend  fun getUserFavPlaylist(){
        var operationResult: OperationResult<favPlaylists> =
            viewModel.getUserFavPodcast(FavPlaylistQuery(curr_passHash,"-1"))

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : favPlaylists = operationResult.data

                if (favTransactionResp.favPlaylists.isEmpty()){
                    binding.favPodcastRC.visibility=View.GONE
                    binding.noFavouriteLayout.visibility = View.VISIBLE
                }
                else{
                    binding.favPodcastRC.visibility=View.VISIBLE
                    binding.noFavouriteLayout.visibility = View.GONE
                }
                favPlaylistRCAdapter.updateItems(favTransactionResp.favPlaylists)

                binding.loadingLayout.visibility= View.GONE
                binding.favPodcastRC.visibility= View.VISIBLE

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