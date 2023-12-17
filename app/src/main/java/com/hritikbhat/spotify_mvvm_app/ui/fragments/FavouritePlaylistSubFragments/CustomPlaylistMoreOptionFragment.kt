package com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouritePlaylistSubFragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.adapters.PlaylistAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentCustomPlaylistMoreOptionBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments.ShowPlaylistSongsFragmentArgs
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import kotlinx.coroutines.launch

class CustomPlaylistMoreOptionFragment : Fragment(){

    private lateinit var viewModel: FavPlaylistViewModel
    private lateinit var binding: FragmentCustomPlaylistMoreOptionBinding
    private lateinit var binding2:FragmentFavouritesBinding

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_custom_playlist_more_option, container, false)

        binding2 = FavouritesFragment.binding


        viewModel = ViewModelProvider(this).get(FavPlaylistViewModel::class.java)


        val customPlaylistMoreOptionFragmentArgs = CustomPlaylistMoreOptionFragmentArgs.fromBundle(requireArguments())

        val playlistData = customPlaylistMoreOptionFragmentArgs.playlistData


        sharedPref = requireContext().getSharedPreferences(
            MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        binding.customPlaylistMoreOptionBackBtn.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.playlistName.text = playlistData.plname

        binding.deletePlaylist.setOnClickListener(View.OnClickListener {
            viewModel.viewModelScope.launch {
                setInitForDeleteCustPlay(playlistData.plid.toString())
            }

        })


        return binding.root
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
//                    getUserFavPlaylist()

//                    binding.favPlaylistRC.visibility=View.VISIBLE
//                    binding.noFavouriteLayout.visibility = View.GONE
//                    binding.playlistRC.visibility = View.GONE
//                    binding.songMoreOptionLayout.visibility=View.GONE
//                    binding.customPlaylistMoreOptionLayout.visibility=View.GONE
//                    binding2.tabLayout.visibility=View.VISIBLE
//                    binding2.viewPager.isUserInputEnabled = true
                    //Go back to favplayliststart page

                    binding2.tabLayout.visibility=View.VISIBLE
                    binding2.viewPager.isUserInputEnabled = true

                    findNavController().popBackStack()
                    findNavController().popBackStack()


                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()
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


}