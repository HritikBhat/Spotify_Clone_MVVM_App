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
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentAddCustomPlaylistBinding
import com.hritikbhat.spotify_mvvm_app.models.CustomPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels.FavPlaylistViewModel
import kotlinx.coroutines.launch

class AddCustomPlaylistFragment : Fragment() {
    private lateinit var viewModel: FavPlaylistViewModel
    private lateinit var binding: FragmentAddCustomPlaylistBinding

    private lateinit var sharedPref: SharedPreferences
    private lateinit var currPassHash:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_custom_playlist, container, false)

        viewModel = ViewModelProvider(this)[FavPlaylistViewModel::class.java]


        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()

        binding.customPlaylistNextBtn.setOnClickListener{
            viewModel.viewModelScope.launch {
                createCustomPlaylist()
            }
        }


        binding.customPlaylistCancelBtn.setOnClickListener{
            binding.customPlaylistNameTT.setText("")
            findNavController().popBackStack()
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



    private suspend fun createCustomPlaylist(){
        val customName = binding.customPlaylistNameTT.text.toString()
        val operationResult: OperationResult<FavTransactionResp> = viewModel.addCustomPlaylist(
            CustomPlaylistQuery(customName,currPassHash)
        )
        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){

                    binding.customPlaylistNameTT.setText("")

                    Toast.makeText(context,favTransactionResp.message, Toast.LENGTH_LONG).show()

                    //Go back to FavPlaylistStart
                    findNavController().popBackStack()

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