package com.hritikbhat.spotify_mvvm_app.ViewModels.SubFragmentsViewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.Models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.CustomPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.Models.OperationResult
import com.hritikbhat.spotify_mvvm_app.Models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.Models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.Models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.ViewModels.SharedViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FavPlaylistViewModel() : SharedViewModel() {
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

    suspend fun getUserFavPlaylist(fQ: FavPlaylistQuery): OperationResult<favPlaylists> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.getUserFavPlaylist(fQ)
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


    suspend fun deleteSongFromPlaylist(fQ: AddSongPlaylistQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.removeSongFromPlaylist(fQ)
                Log.d("CHEEZY_NOTIFICATION",response.toString())
                if (response.body() != null) {
                    val favTransaction: FavTransactionResp? = response.body()
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

    suspend fun getFavSongsDetails(plq: PlayListQuery): OperationResult<PlayListDetail> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",plq.toString())
                val response = apiService.getUserFavouriteSongs(plq)
                Log.d("CHEEZY_NOTIFICATION",response.toString())
                if (response.body() != null) {
                    val playListDetails: PlayListDetail? = response.body()
                    if (playListDetails != null) {
                        Log.d("CHEEZY_NOTIFICATION",playListDetails.toString())

                        continuation.resume(OperationResult.Success(playListDetails)) // Resume with true when successful
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

    suspend fun addCustomPlaylist(customPlaylistQuery: CustomPlaylistQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",customPlaylistQuery.toString())
                val response = apiService.addCustomPlaylist(customPlaylistQuery)
                Log.d("CHEEZY_NOTIFICATION",response.toString())
                if (response.body() != null) {
                    val favTransaction: FavTransactionResp? = response.body()
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

    suspend fun removeCustomPlayList(fQ: AddSongPlaylistQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.deleteCustomPlaylist(fQ)
                Log.d("CHEEZY_NOTIFICATION",response.toString())
                if (response.body() != null) {
                    val favTransaction: FavTransactionResp? = response.body()
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