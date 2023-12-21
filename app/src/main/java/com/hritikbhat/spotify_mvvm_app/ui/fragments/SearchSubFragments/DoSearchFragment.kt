package com.hritikbhat.spotify_mvvm_app.ui.Fragments.SearchSubFragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.adapters.SearchAdapter
import com.hritikbhat.spotify_mvvm_app.databinding.FragmentDoSearchBinding
import com.hritikbhat.spotify_mvvm_app.models.AllSearchItem
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.viewModels.SearchViewModel
import kotlinx.coroutines.launch

class DoSearchFragment : Fragment(),SearchAdapter.OnItemClickListener {
    private var _binding: FragmentDoSearchBinding?=null
    private val binding get() = _binding!!
    private val searchRCAdapter = SearchAdapter()

    private lateinit var sharedPref: SharedPreferences
    private lateinit var currPassHash:String

    private lateinit var viewModel: SearchViewModel

    private val handler = Handler(Looper.myLooper()!!)

    private lateinit var searchNavController: NavController






    override fun onPause() {
        super.onPause()
        if (binding !=null){
            binding.searchEditText.setText("")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_do_search, container, false)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        sharedPref = SharedPreferenceInstance(requireContext()).getSPInstance()
        currPassHash = sharedPref.getString("passHash", "").toString()

        searchNavController = findNavController()

        searchRCAdapter.setOnItemClickListener(this)
        binding.searchRc.layoutManager = LinearLayoutManager(context)
        binding.searchRc.adapter = searchRCAdapter


        binding.searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove any previously scheduled processing
                handler.removeCallbacksAndMessages(null)


                val searchQuery = binding.searchEditText.text.toString()

                if (searchQuery.isEmpty()){
                    binding.searchStartLayout.visibility = View.VISIBLE
                    binding.searchRc.visibility = View.GONE
                    binding.notFoundLayout.visibility = View.GONE
                    binding.loadingLayout.visibility= View.GONE
                    return
                }
                else{
                    binding.searchStartLayout.visibility= View.GONE
                    binding.loadingLayout.visibility= View.VISIBLE
                    binding.searchRc.visibility = View.GONE
                    binding.notFoundLayout.visibility = View.GONE
                }

                // Schedule data processing after 2 seconds
                handler.postDelayed({
                    viewModel.viewModelScope.launch {
                        getSearchResult(searchQuery)
                    }


                }, 1000) // 5000 milliseconds = 5 seconds
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        return binding.root
    }

    suspend  fun getSearchResult(searchQuery: String){
        when (val operationResult: OperationResult<List<AllSearchItem>> = viewModel.searchResult(currPassHash,searchQuery)) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result

                val searchList : List<AllSearchItem> = operationResult.data
                searchRCAdapter.updateItems(searchList)
                if (searchList.isEmpty()){
                    binding.searchQueryText.text = "${binding.searchEditText.text}"
                    binding.searchStartLayout.visibility = View.GONE
                    binding.searchRc.visibility = View.GONE
                    binding.notFoundLayout.visibility = View.VISIBLE
                    binding.loadingLayout.visibility= View.GONE

                }
                else{
                    binding.searchStartLayout.visibility = View.GONE
                    binding.searchRc.visibility = View.VISIBLE
                    binding.notFoundLayout.visibility = View.GONE
                    binding.loadingLayout.visibility= View.GONE
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

    override fun onSearchItemClick(plid: Int, pname: String, aname: String, ptype: Int) {
        viewModel.viewModelScope.launch {
           //Send to ShowPlaylistSongsFragment
            val playlist = Playlist(plid,pname,ptype,aname)

            val showPlaylistSongsFragmentAction = DoSearchFragmentDirections.actionDoSearchFragmentToShowPlaylistSongsFragment(playlist)
            searchNavController.navigate(showPlaylistSongsFragmentAction)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
        _binding = null
    }
}