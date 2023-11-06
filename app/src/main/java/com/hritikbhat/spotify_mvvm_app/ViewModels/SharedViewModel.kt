package com.hritikbhat.spotify_mvvm_app.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.Models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.Models.OperationResult
import com.hritikbhat.spotify_mvvm_app.Models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.Models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.Models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class SharedViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)


    suspend fun getPlaylistDetails(plq: PlayListQuery): OperationResult<PlayListDetail> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",plq.toString())
                val response = apiService.getPlaylistDetails(plq)
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

    suspend fun addFavPlaylist(fQ: FavPlaylistQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.addFavPlayList(fQ)
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

    suspend fun removeFavPlaylist(fQ: FavPlaylistQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.removeFavPlayList(fQ)
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

    suspend fun addFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.addFavSong(fQ)
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

    suspend fun removeFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.removeFavSong(fQ)
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


    suspend fun getUserCustomFavPlaylist(fQ: FavPlaylistQuery): OperationResult<favPlaylists> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.getUserCustomFavPlaylist(fQ)
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


    suspend fun addSongToPlaylist(fQ: AddSongPlaylistQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.addSongToPlaylist(fQ)
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