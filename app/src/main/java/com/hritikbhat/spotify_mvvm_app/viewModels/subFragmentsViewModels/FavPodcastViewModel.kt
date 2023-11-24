package com.hritikbhat.spotify_mvvm_app.viewModels.subFragmentsViewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.viewModels.SharedViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FavPodcastViewModel : SharedViewModel() {
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

    suspend fun getUserFavPodcast(fQ: FavPlaylistQuery): OperationResult<favPlaylists> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.getUserFavPodcast(fQ)
                Log.d("CHEEZY_NOTIFICATION",response.toString())
                if (response.body() != null) {
                    val favTransaction: favPlaylists? = response.body()
                    if (favTransaction != null) {
                        Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())

                        continuation.resume(OperationResult.Success(favTransaction)) // Resume with true when successful
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