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
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.Utils.TransactionTypes
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentShowPlaylistSongsBinding
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import kotlinx.coroutines.launch

class ShowPlaylistSongsFragment : Fragment(),PlaylistAdapter.OnItemClickListener {

    private lateinit var binding: FragmentShowPlaylistSongsBinding

    private var _playlistAdapter: PlaylistAdapter? = null
    private val playlistAdapter get() = _playlistAdapter!!
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currPassHash:String
    private var isFragmentActive = false
    private lateinit var viewModel: SearchViewModel
    private lateinit var viewModel2: FavPlaylistViewModel

    private lateinit var playlistData: Playlist

    override fun onResume() {
        isFragmentActive=true
        super.onResume()
    }

    override fun onPause() {
        isFragmentActive=false
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_playlist_songs, container, false)

        _playlistAdapter = PlaylistAdapter()
        try {
            FavouritesFragment.tabLayout?.visibility = View.GONE
            FavouritesFragment.viewPager?.isUserInputEnabled = false
        }catch (_:Exception){
        }


        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        viewModel2 = ViewModelProvider(this)[FavPlaylistViewModel::class.java]

        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()

        // Set up RecyclerView
        binding.playlistRC.layoutManager = LinearLayoutManager(context)

        val showPlaylistSongsFragmentArgs = ShowPlaylistSongsFragmentArgs.fromBundle(requireArguments())



        playlistData = showPlaylistSongsFragmentArgs.playlistObjData


        viewModel.viewModelScope.launch {
            getPlaylistDetails(playlistData.plname,playlistData.aname,playlistData.pltype,PlayListQuery(playlistData.plid.toString(),currPassHash))
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
                        onBackButtonClicked()
                    }

                }
            }
        )

        return binding.root
    }

    suspend  fun getPlaylistDetails(pname: String, aname: String, ptype: Int, plq: PlayListQuery){

        val operationResult: OperationResult<PlayListDetail> = if(playlistData.plid==-1){
            //Fav Songs Section
            viewModel2.getFavSongsDetails(plq)
        } else{
            viewModel.getPlaylistDetails(plq)
        }

        Log.d("CHEEZY NOTIFICATION","API Array: ${operationResult.toString()}")

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val playListDetails : PlayListDetail = operationResult.data
                Log.d("CHEEZY NOTIFICATION","API Array: ${playListDetails.toString()}")
                playlistAdapter.setPlaylistItems(plq.plid,pname,aname,ptype, playListDetails.isFav,playListDetails)
                binding.playlistRC.adapter = playlistAdapter
                playlistAdapter.setOnItemClickListener(this)
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

    override fun onBackButtonClicked() {
        FavouritesFragment.tabLayout?.visibility = View.VISIBLE
        FavouritesFragment.viewPager?.isUserInputEnabled = true
        if (isFragmentActive){
            findNavController().popBackStack()
        }
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
                setFavPlaylistStatus(FavPlaylistQuery(currPassHash,plid.toString()),
                    TransactionTypes.INSERT_TRANSACTION
                )
            }
        }
    }

    private suspend  fun setFavPlaylistStatus(fQ: FavPlaylistQuery, transType:TransactionTypes){
        val operationResult: OperationResult<FavTransactionResp> = if (transType==TransactionTypes.INSERT_TRANSACTION){
            viewModel.addFavPlaylist(fQ)
        } else{
            viewModel.removeFavPlaylist(fQ)
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
        val action = ShowPlaylistSongsFragmentDirections.actionShowPlaylistSongsFragmentToSongMoreOptionFragment(items[pos])
        action.plid = plid
        action.position = pos
        action.ptype = ptype

        findNavController().navigate(action)
    }

    // In Progress
    override fun onItemPlaylistMoreOptionClick(plid: String, pname: String, ptype: Int) {
        //Send to PlaylistMoreOption
        if (ptype==3){
            val action = ShowPlaylistSongsFragmentDirections.actionShowPlaylistSongsFragmentToCustomPlaylistMoreOptionFragment2(playlistData)
            findNavController().navigate(action)
        }
        else{
            //Move to Album Playlist More Option
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _playlistAdapter = null
    }


}