package com.hritikbhat.spotify_mvvm_app.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.adapters.RecentAdapter
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.viewModels.HomeViewModel
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentHomeBinding
import com.hritikbhat.spotify_mvvm_app.ui.activities.SettingsActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {



    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var context: Context

    private val recentRCAdapter = RecentAdapter()

    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"
    private lateinit var curr_passHash:String

    override fun onResume() {
        super.onResume()
        viewModel.viewModelScope.launch {
            getRecents()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        context= this.requireContext()

        sharedPref = context.getSharedPreferences(MY_PREFS_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        curr_passHash = sharedPref.getString("passHash", "").toString()

        val view = binding.root

        binding.homesSettingBtn.setOnClickListener(View.OnClickListener {
            startSettingActivity(context)
        })

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        binding.recentPlayedRC.layoutManager = layoutManager

        binding.recentPlayedRC.adapter = recentRCAdapter

        viewModel.viewModelScope.launch {
            getRecents()
        }




        return view
    }

    suspend  fun getRecents(){
        val operationResult: OperationResult<List<Song>> = viewModel.getRecent(curr_passHash)

        when (operationResult) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val searchList : List<Song> = operationResult.data
                if (searchList!=null){
                    recentRCAdapter.updateItems(searchList)
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


    private fun startSettingActivity(context: Context) {
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(intent)
    }

}