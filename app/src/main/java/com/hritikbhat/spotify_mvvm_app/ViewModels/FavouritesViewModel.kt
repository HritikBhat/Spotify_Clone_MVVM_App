package com.hritikbhat.spotify_mvvm_app.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.Models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.Models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.Models.OperationResult
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FavouritesViewModel : SharedViewModel() {
    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

//    suspend fun addFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
//        viewModelScope.launch {
//            try {
//                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
//                val response = apiService.addFavSong(fQ)
//                Log.d("CHEEZY_NOTIFICATION",response.toString())
//                if (response.body() != null) {
//                    val favTransaction: FavTransactionResp? = response.body()
//                    if (favTransaction != null) {
//                        Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())
//
//                        continuation.resume(OperationResult.Success(favTransaction)) // Resume with true when successful
//                    } else {
//                        continuation.resume(OperationResult.Error("Search result is null")) // Resume with false when auth is null
//                    }
//                } else {
//                    continuation.resume(OperationResult.Error("Network error"))
//                }
//            } catch (e: Exception) {
//                Log.e("ERROR", e.message.toString())
//                continuation.resume(OperationResult.Error("Exception: ${e.message}"))
//            }
//        }
//    }
//
//    suspend fun removeFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
//        viewModelScope.launch {
//            try {
//                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
//                val response = apiService.removeFavSong(fQ)
//                Log.d("CHEEZY_NOTIFICATION",response.toString())
//                if (response.body() != null) {
//                    val favTransaction: FavTransactionResp? = response.body()
//                    if (favTransaction != null) {
//                        Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())
//
//                        continuation.resume(OperationResult.Success(favTransaction)) // Resume with true when successful
//                    } else {
//                        continuation.resume(OperationResult.Error("Search result is null")) // Resume with false when auth is null
//                    }
//                } else {
//                    continuation.resume(OperationResult.Error("Network error"))
//                }
//            } catch (e: Exception) {
//                Log.e("ERROR", e.message.toString())
//                continuation.resume(OperationResult.Error("Exception: ${e.message}"))
//            }
//        }
//    }

    suspend fun addRecentSong(fQ: FavSongQuery): OperationResult<FavTransactionResp> = suspendCoroutine { continuation ->
        viewModelScope.launch {
            try {
                Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                val response = apiService.addRecentSong(fQ)
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