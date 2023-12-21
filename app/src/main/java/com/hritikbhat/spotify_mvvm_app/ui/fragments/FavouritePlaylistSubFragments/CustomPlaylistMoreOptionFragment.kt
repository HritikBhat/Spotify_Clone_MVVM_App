package com.hritikbhat.spotify_mvvm_app.ui.Fragments.FavouritePlaylistSubFragments

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
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentCustomPlaylistMoreOptionBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavouritesBinding
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.ui.fragments.FavouritesFragment
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import kotlinx.coroutines.launch

class CustomPlaylistMoreOptionFragment : Fragment(){

    private lateinit var viewModel: FavPlaylistViewModel
    private lateinit var binding: FragmentCustomPlaylistMoreOptionBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currPassHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_custom_playlist_more_option, container, false)



        viewModel = ViewModelProvider(this)[FavPlaylistViewModel::class.java]


        val customPlaylistMoreOptionFragmentArgs = CustomPlaylistMoreOptionFragmentArgs.fromBundle(requireArguments())

        val playlistData = customPlaylistMoreOptionFragmentArgs.playlistData

        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()

        binding.customPlaylistMoreOptionBackBtn.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.playlistName.text = playlistData.plname

        binding.deletePlaylist.setOnClickListener{
            viewModel.viewModelScope.launch {
                setInitForDeleteCustPlay(playlistData.plid.toString())
            }
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
                        findNavController().popBackStack()
                    }
                }
            }
        )

        return binding.root
    }

    private suspend fun setInitForDeleteCustPlay(plid: String) {

        val operationResult: OperationResult<FavTransactionResp> = viewModel.removeCustomPlayList(
            AddSongPlaylistQuery(currPassHash,plid,"")
        )

        when (operationResult) {
            is OperationResult.Success -> {



                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
                    //Go back to favPlaylistStart page

                    FavouritesFragment.tabLayout?.visibility=View.VISIBLE
                    FavouritesFragment.viewPager?.isUserInputEnabled = true

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