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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.adapters.AddToPlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentDoSearchBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentShowPlaylistSongsBinding
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
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
    private lateinit var binding2: FragmentFavouritesBinding

    private val playlistAdapter = PlaylistAdapter()
    private lateinit var sharedPref: SharedPreferences
    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0

    private lateinit var viewModel: SearchViewModel
    private lateinit var viewModel2: FavPlaylistViewModel

    private lateinit var playlistData: Playlist


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_playlist_songs, container, false)

        try {
            binding2 = FavouritesFragment.binding
            binding2.tabLayout.visibility = View.GONE
            binding2.viewPager.isUserInputEnabled = false
        }catch (e:Exception){
        }


        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        viewModel2 = ViewModelProvider(this).get(FavPlaylistViewModel::class.java)

        sharedPref = requireContext().getSharedPreferences(MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        // Set up RecyclerView
        binding.playlistRC.layoutManager = LinearLayoutManager(context)

        val showPlaylistSongsFragmentArgs = ShowPlaylistSongsFragmentArgs.fromBundle(requireArguments())



        playlistData = showPlaylistSongsFragmentArgs.playlistObjData


        viewModel.viewModelScope.launch {
            getPlaylistDetails(playlistData.plname,playlistData.aname,playlistData.pltype,PlayListQuery(playlistData.plid.toString(),curr_passHash))
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

    override fun onBackButtonClicked() {
        if (::binding2.isInitialized){
            binding2.tabLayout.visibility = View.VISIBLE
            binding2.viewPager.isUserInputEnabled = true
        }
        findNavController().popBackStack()
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


}