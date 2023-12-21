package com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouriteAlbumSubFragments

import android.content.SharedPreferences
import android.os.Bundle

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.adapters.FavPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouriteAlbumStartBinding
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavAlbumViewModel
import kotlinx.coroutines.launch

class FavouriteAlbumStartFragment : Fragment(),FavPlaylistAdapter.OnItemClickListener {
    private lateinit var binding: FragmentFavouriteAlbumStartBinding
    private var _favPlaylistRCAdapter:FavPlaylistAdapter? = null
    private val favPlaylistRCAdapter get() = _favPlaylistRCAdapter!!

    private lateinit var viewModel: FavAlbumViewModel


    private lateinit var sharedPref: SharedPreferences

    private lateinit var currPassHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_album_start, container, false)

        _favPlaylistRCAdapter = FavPlaylistAdapter()

        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()
        viewModel = ViewModelProvider(this).get(FavAlbumViewModel::class.java)


        // Set up RecyclerView
        binding.favAlbumRC.layoutManager = LinearLayoutManager(context)
        binding.favAlbumRC.adapter = favPlaylistRCAdapter

        favPlaylistRCAdapter.setOnItemClickListener(this)

        viewModel.viewModelScope.launch {
            getUserFavPlaylist()
        }

        return binding.root
    }

    private suspend  fun getUserFavPlaylist(){
        val operationResult: OperationResult<favPlaylists> =
            viewModel.getUserFavAlbum(FavPlaylistQuery(currPassHash,"-1"))

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

    override fun onItemClick(plid: Int, pname: String, ptype: Int, aname: String) {
            //getPlaylistDetails(pname,aname,ptype, PlayListQuery(plid.toString(),curr_passHash))
            //Send this to showPlaylist

            val playlist = Playlist(plid,pname,ptype,aname)

            val showPlaylistSongsFragmentAction = FavouriteAlbumStartFragmentDirections.actionFavouriteAlbumStartFragmentToShowPlaylistSongsFragmentFavPlaylist(playlist)
            findNavController().navigate(showPlaylistSongsFragmentAction)
    }

    override fun onDestroy() {
        super.onDestroy()
        _favPlaylistRCAdapter = null
    }

}