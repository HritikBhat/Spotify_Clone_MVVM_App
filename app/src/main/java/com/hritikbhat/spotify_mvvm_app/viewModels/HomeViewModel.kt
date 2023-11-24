package com.hritikbhat.spotify_mvvm_app.viewModels

import androidx.lifecycle.ViewModel
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.utils.Classes.RecentlyPlayedSongManager
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper

class HomeViewModel : ViewModel() {

    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

    private val recentlyPlayedSongManager = RecentlyPlayedSongManager(apiService)

    suspend fun getRecent(pashHash:String): OperationResult<List<Song>>{
        return recentlyPlayedSongManager.getRecent(pashHash)
    }


}