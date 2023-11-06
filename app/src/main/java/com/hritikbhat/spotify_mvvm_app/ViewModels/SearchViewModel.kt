package com.hritikbhat.spotify_mvvm_app.ViewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.Models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.Models.AllSearchItem
import com.hritikbhat.spotify_mvvm_app.Models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.Models.OperationResult
import com.hritikbhat.spotify_mvvm_app.Models.SearchList
import com.hritikbhat.spotify_mvvm_app.Models.SearchQuery
import com.hritikbhat.spotify_mvvm_app.Models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SearchViewModel() : SharedViewModel() {

    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)


    private fun setResult(results: SearchList): MutableList<AllSearchItem> {
        val result = mutableListOf<AllSearchItem>()
        // Extract ids and names from the 'artists' list
        results.artists.forEach { artist ->
            result.add(AllSearchItem(artist.aid,-1,artist.aname,0, emptyList(),false))
        }

        // Extract ids and names from the 'playlists' list
        results.playlist.forEach { playlist ->
            result.add(AllSearchItem(playlist.plid,-1,playlist.plname,1, arrayListOf(playlist.aname),false))
        }

        // Extract ids and names from the 'songs' list
        results.song.forEach { song ->
            result.add(AllSearchItem(song.sid,song.albumId,song.sname,2,song.artist_name_arr,song.isFav))
        }

        return result
    }


    suspend fun searchResult(pashHash:String,searchQuery: String): OperationResult<List<AllSearchItem>> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",searchQuery)
                val response = apiService.searchResults(SearchQuery(pashHash,searchQuery))
                if (response.body() != null) {
                    val searchResult: SearchList? = response.body()
                    if (searchResult != null) {
                        Log.d("CHEEZY_NOTIFICATION",searchResult.toString())

                        continuation.resume(OperationResult.Success(setResult(searchResult))) // Resume with true when successful
                    } else {
                        continuation.resume(OperationResult.Error("Search result is null")) // Resume with false when auth is null
                    }
                } else {
                    continuation.resume(OperationResult.Error("Network error"))
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
                continuation.resume(OperationResult.Error("Exception: ${e.message}"))
            }
        }
    }



}