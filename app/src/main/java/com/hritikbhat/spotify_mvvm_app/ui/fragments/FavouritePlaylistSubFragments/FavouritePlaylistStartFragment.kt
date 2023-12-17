package com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouritePlaylistSubFragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.adapters.FavPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritePlaylistStartBinding
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistsX
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments.DoSearchFragmentDirections
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import kotlinx.coroutines.launch

class FavouritePlaylistStartFragment : Fragment(),FavPlaylistAdapter.OnItemClickListener {

    private lateinit var viewModel: FavPlaylistViewModel
    private lateinit var binding: FragmentFavouritePlaylistStartBinding

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    private val favPlaylistRCAdapter = FavPlaylistAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_playlist_start, container, false)



        sharedPref = requireContext().getSharedPreferences(
            MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        binding.favPlaylistRC.layoutManager = LinearLayoutManager(context)

        viewModel = ViewModelProvider(this).get(FavPlaylistViewModel::class.java)


        viewModel.viewModelScope.launch {
            getUserFavPlaylist()
        }

        return binding.root
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
                binding.favPlaylistRC.adapter = favPlaylistRCAdapter
                favPlaylistRCAdapter.setOnItemClickListener(this)

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

    override fun onItemClick(plid: Int, pname: String, ptype: Int, aname: String) {
        viewModel.viewModelScope.launch {
            if (plid==-2){
//                binding.customPlaylistLayout.visibility=View.VISIBLE
//                binding.noFavouriteLayout.visibility=View.GONE
//                binding.favPlaylistRC.visibility = View.GONE
//                binding2.tabLayout.visibility=View.GONE
//                binding2.viewPager.isUserInputEnabled = false

                //Send to Custom Playlist Creation

                findNavController().navigate(R.id.action_favouritePlaylistStartFragment_to_addCustomPlaylistFragment)

            }
            else{
                //Go to show playlist

                val playlist = Playlist(plid,pname,ptype,aname)

                val showPlaylistSongsFragmentAction = FavouritePlaylistStartFragmentDirections.actionFavouritePlaylistStartFragmentToShowPlaylistSongsFragmentFavPlaylist(playlist)
                findNavController().navigate(showPlaylistSongsFragmentAction)

            }

        }
    }
}