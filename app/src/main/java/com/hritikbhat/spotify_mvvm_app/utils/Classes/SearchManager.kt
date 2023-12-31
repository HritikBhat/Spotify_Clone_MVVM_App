package com.hritikbhat.spotify_mvvm_app.utils.Classes

import android.util.Log
import com.hritikbhat.spotify_mvvm_app.models.AllSearchItem
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.models.SearchList
import com.hritikbhat.spotify_mvvm_app.models.SearchQuery
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class SearchManager(private val apiService: ApiService) {

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


    suspend fun searchResult(pashHash:String,searchQuery: String): OperationResult<List<AllSearchItem>>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",searchQuery)
                    val response = apiService.searchResults(SearchQuery(pashHash,searchQuery))
                    if (response.body() != null) {
                        val searchResult: SearchList? = response.body()
                        if (searchResult != null) {
                            OperationResult.Success(setResult(searchResult))
                        } else {
                            OperationResult.Error("Search result is null") // Resume with false when auth is null
                        }
                    } else {
                        OperationResult.Error("Network error")
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                    OperationResult.Error("Exception: ${e.message}")
                }
            }
        }
        catch (cancelError: CancellationException){
            cancelError.printStackTrace()
            return OperationResult.Error("Exception: ${cancelError.message}")
        }
    }


    suspend fun getExplorePlaylists(pashHash:String): OperationResult<List<Playlist>>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    val response = apiService.getExplorePlaylists(SearchQuery(pashHash,""))
                    if (response.body() != null) {
                        val searchResult: SearchList? = response.body()
                        if (searchResult != null) {
                            OperationResult.Success(searchResult.playlist)
                        } else {
                            OperationResult.Error("Search result is null") // Resume with false when auth is null
                        }
                    } else {
                        OperationResult.Error("Network error")
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                    OperationResult.Error("Exception: ${e.message}")
                }
            }
        }
        catch (cancelError: CancellationException){
            cancelError.printStackTrace()
            return OperationResult.Error("Exception: ${cancelError.message}")
        }
    }


}