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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentAddCustomPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentAddToCustomPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentFavPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.models.CustomPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistsX
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import kotlinx.coroutines.launch

class AddCustomPlaylistFragment : Fragment() {
    private lateinit var viewModel: FavPlaylistViewModel
    private lateinit var binding: FragmentAddCustomPlaylistBinding

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_custom_playlist, container, false)

        viewModel = ViewModelProvider(this).get(FavPlaylistViewModel::class.java)


        sharedPref = requireContext().getSharedPreferences(
            MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        binding.customPlaylistNextBtn.setOnClickListener(View.OnClickListener {
            viewModel.viewModelScope.launch {
                createCustomPlaylist()
            }
        })


        binding.customPlaylistCancelBtn.setOnClickListener(View.OnClickListener {
            binding.customPlaylistNameTT.setText("")
            findNavController().popBackStack()
        })

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



    private suspend fun createCustomPlaylist(){
        val customName = binding.customPlaylistNameTT.text.toString()
        var operationResult: OperationResult<FavTransactionResp> = viewModel.addCustomPlaylist(
            CustomPlaylistQuery(customName,curr_passHash)
        )
        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){

//                    getUserFavPlaylist()

                    binding.customPlaylistNameTT.setText("")

                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()


                    //Go back to FavPlaylistStart
                    findNavController().popBackStack()


//                    binding.customPlaylistLayout.visibility=View.GONE

//                    binding.noFavouriteLayout.visibility=View.GONE
//                    binding.favPlaylistRC.visibility = View.VISIBLE
//                    binding2.tabLayout.visibility=View.VISIBLE
//                    binding2.viewPager.isUserInputEnabled = true

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

}