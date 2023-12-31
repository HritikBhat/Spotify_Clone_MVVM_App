package com.hritikbhat.spotify_mvvm_app.ui.Fragments.BaseFragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistGridItemAdapter
import com.hritikbhat.spotify_mvvm_app.adapters.RecentAdapter
import com.hritikbhat.spotify_mvvm_app.viewModels.HomeViewModel
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentHomeBinding
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.ui.activities.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(),PlaylistGridItemAdapter.OnItemClickListener {

    private lateinit var viewModel: HomeViewModel
    private var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!

    private var _playlistGridItemAdapter:PlaylistGridItemAdapter? = null
    private val playlistGridItemAdapter get() = _playlistGridItemAdapter!!

    private lateinit var sharedPref: SharedPreferences
    private lateinit var currPassHash:String

    private var _recentRCAdapter: RecentAdapter? = null
    private val recentRCAdapter get() = _recentRCAdapter

    private fun startSettingActivity() {
        val intent = Intent(requireContext(), SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.Main){
                getRecent()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

//        _recentRCAdapter = RecentAdapter()

        // Initialize the ViewModel

        _recentRCAdapter = RecentAdapter()
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()


        binding.homesSettingBtn.setOnClickListener{
            startSettingActivity()
        }

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        binding.recentPlayedRC.layoutManager = layoutManager

        binding.recentPlayedRC.adapter = recentRCAdapter

        viewModel.viewModelScope.launch {
            withContext(Dispatchers.Main){
                getExplorePlaylists()
            }
            getRecent()
            val layoutManager = GridLayoutManager(requireContext(),2)
            binding.homeRV.layoutManager = layoutManager
            binding.homeRV.adapter = playlistGridItemAdapter
            binding.homeRV.isNestedScrollingEnabled = false
        }

        return binding.root
    }

    private suspend fun getExplorePlaylists(){
        when (val operationResult: OperationResult<List<Playlist>> = viewModel.getExplorePlaylists(currPassHash)) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val getExplorePlayList : List<Playlist> = operationResult.data
                Log.d("Explore Playlist",getExplorePlayList.toString())

                if (getExplorePlayList.isNotEmpty()){
//                    recentRCAdapter.updateItems(searchList)
                    _playlistGridItemAdapter= PlaylistGridItemAdapter(requireContext())
                    playlistGridItemAdapter.setOnItemClickListener(this)
                    playlistGridItemAdapter.setExplorePlaylistItems(getExplorePlayList)
                    val layoutManager = GridLayoutManager(requireContext(),2)
                    binding.homeRV.layoutManager = layoutManager
                    binding.homeRV.adapter = playlistGridItemAdapter
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

    private suspend  fun getRecent(){

        when (val operationResult: OperationResult<List<Song>> = viewModel.getRecent(currPassHash)) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val searchList : List<Song> = operationResult.data

                recentRCAdapter?.updateItems(searchList)

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




    override fun onDestroy() {
        super.onDestroy()
        _playlistGridItemAdapter = null
        _recentRCAdapter = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
        _binding = null

    }

    override fun onPlaylistItemClick(plid: Int, plname: String, aname: String, ptype: Int) {
        viewModel.viewModelScope.launch {
            //Send to ShowPlaylistSongsFragment
            Log.d("Clicked GridItem",plname)
            val playlist = Playlist(plid,plname,ptype,aname)

            val showPlaylistSongsFragmentAction = HomeFragmentDirections.actionNavigationHomeToSearchNavGraph(playlist)
            findNavController().navigate(showPlaylistSongsFragmentAction)
        }
    }

}