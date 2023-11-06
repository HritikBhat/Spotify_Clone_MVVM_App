package com.hritikbhat.spotify_mvvm_app.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.Models.AllSearchItem
import com.hritikbhat.spotify_mvvm_app.Models.OperationResult
import com.hritikbhat.spotify_mvvm_app.Models.SearchList
import com.hritikbhat.spotify_mvvm_app.Models.SearchQuery
import com.hritikbhat.spotify_mvvm_app.Models.Song
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeViewModel : ViewModel() {

    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)


    suspend fun getRecent(pashHash:String): OperationResult<List<Song>> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                val response = apiService.getUserRecent(SearchQuery(pashHash,""))
                if (response.body() != null) {
                    val searchResult: SearchList? = response.body()
                    if (searchResult != null) {
                        Log.d("CHEEZY_NOTIFICATION",searchResult.toString())

                        continuation.resume(OperationResult.Success(searchResult.song)) // Resume with true when successful
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