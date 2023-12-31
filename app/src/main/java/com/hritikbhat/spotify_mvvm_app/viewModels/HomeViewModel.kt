package com.hritikbhat.spotify_mvvm_app.viewModels

import androidx.lifecycle.ViewModel
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.utils.Classes.RecentlyPlayedSongManager
import com.hritikbhat.spotify_mvvm_app.utils.Classes.SearchManager
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper

class HomeViewModel : ViewModel() {

    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

    private val recentlyPlayedSongManager = RecentlyPlayedSongManager(apiService)

    private val searchManager = SearchManager(apiService)

    suspend fun getRecent(pashHash:String): OperationResult<List<Song>>{
        return recentlyPlayedSongManager.getRecent(pashHash)
    }

    suspend fun getExplorePlaylists(passHash:String): OperationResult<List<Playlist>> {
        return searchManager.getExplorePlaylists(passHash)
    }


}