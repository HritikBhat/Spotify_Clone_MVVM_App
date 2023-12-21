package com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouriteSubFragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.adapters.FavPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPodcastViewModel
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.Utils.TransactionTypes
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavPodcastBinding
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import kotlinx.coroutines.launch

class FavPodcastFragment : Fragment(), FavPlaylistAdapter.OnItemClickListener,PlaylistAdapter.OnItemClickListener {
    private lateinit var viewModel: FavPodcastViewModel
    private lateinit var binding: FragmentFavPodcastBinding

    private lateinit var context: Context

    private val favPlaylistRCAdapter = FavPlaylistAdapter()
    private val playlistAdapter = PlaylistAdapter()

    private lateinit var sharedPref: SharedPreferences
    
    private lateinit var currPassHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_podcast, container, false)
        context = binding.root.context
        sharedPref = SharedPreferenceInstance(context).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()

        favPlaylistRCAdapter.setOnItemClickListener(this)
        playlistAdapter.setOnItemClickListener(this)

        viewModel = ViewModelProvider(this)[FavPodcastViewModel::class.java]

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
                        FavouritesFragment.tabLayout?.visibility = View.VISIBLE
                        FavouritesFragment.viewPager?.isUserInputEnabled = true
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

    override fun onItemClick(plid: Int, plName: String, ptype: Int, aname: String) {
        viewModel.viewModelScope.launch {
            getPlaylistDetails(plName,aname,ptype,PlayListQuery(plid.toString(),currPassHash))
        }
    }

    override fun onBackButtonClicked() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun onFavPlaylistButtonClick(isFav: Boolean,plid:Int) {
        if (isFav){

            //DeleteFavSong
            viewModel.viewModelScope.launch {
                setFavPlaylistStatus(FavPlaylistQuery(currPassHash,plid.toString()),TransactionTypes.DELETE_TRANSACTION)
            }
        }
        else{

            //AddFavSong
            viewModel.viewModelScope.launch {
                setFavPlaylistStatus(FavPlaylistQuery(currPassHash,plid.toString()),TransactionTypes.INSERT_TRANSACTION)
            }
        }
    }

    override fun onItemMoreOptionClick(plid: String,items: MutableList<Song>, i: Int, ptype: Int) {

    }

    override fun onItemPlaylistMoreOptionClick(plid: String, pname: String, ptype: Int) {
    }

    private suspend  fun setFavPlaylistStatus(fQ: FavPlaylistQuery, transType:TransactionTypes){
        val operationResult: OperationResult<FavTransactionResp>
        if (transType== TransactionTypes.INSERT_TRANSACTION){

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

        when (val operationResult: OperationResult<PlayListDetail> = viewModel.getPlaylistDetails(plq)) {
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
                FavouritesFragment.tabLayout?.visibility = View.GONE
                FavouritesFragment.viewPager?.isUserInputEnabled = false
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
        val operationResult: OperationResult<favPlaylists> =
            viewModel.getUserFavPodcast(FavPlaylistQuery(currPassHash,"-1"))

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