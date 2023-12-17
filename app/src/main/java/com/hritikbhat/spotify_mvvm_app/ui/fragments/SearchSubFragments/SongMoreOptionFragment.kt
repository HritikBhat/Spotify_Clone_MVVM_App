package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

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
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentPlaylistMoreOptionBinding
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import kotlinx.coroutines.launch

class SongMoreOptionFragment : Fragment() {

    private val INSERTTRANSACTION =1
    private  val DELETETRANSACTION=0

    private lateinit var binding: FragmentPlaylistMoreOptionBinding
    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String
    private lateinit var sharedPref: SharedPreferences

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        sharedPref = requireContext().getSharedPreferences(MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_more_option, container, false)



        return binding.root
    }

    suspend  fun setFavSongStatus(fQ: FavSongQuery, transType:Int){
        var operationResult: OperationResult<FavTransactionResp>
        if (transType==INSERTTRANSACTION){

            operationResult = viewModel.addFavSong(fQ)
        }
        else{
            operationResult= viewModel.removeFavSong(fQ)
        }

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val favTransactionResp : FavTransactionResp = operationResult.data

                if (favTransactionResp.success){
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