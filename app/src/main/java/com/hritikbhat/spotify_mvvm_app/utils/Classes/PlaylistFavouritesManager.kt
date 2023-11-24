package com.hritikbhat.spotify_mvvm_app.utils.Classes

import android.util.Log
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class PlaylistFavouritesManager(private val apiService: ApiService) {

    suspend fun addFavPlaylist(fQ: FavPlaylistQuery): OperationResult<FavTransactionResp>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.addFavPlayList(fQ)
                    Log.d("CHEEZY_NOTIFICATION",response.toString())
                    if (response.body() != null) {
                        val favTransaction: FavTransactionResp? = response.body()
                        if (favTransaction != null) {
                            Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())
                            OperationResult.Success(favTransaction) // Resume with true when successful
                        } else {
                            OperationResult.Error("Search result is null") // Resume with false when auth is null
                        }
                    } else {
                        OperationResult.Error("Network error")
                    }
                }
                catch (cancelError:CancellationException){
                    Log.e("PlaylistFavouritesManager Cancellation ERROR", cancelError.message.toString())
                    OperationResult.Error("Something Gone Wrong")
                }
                catch (e: Exception) {
                    Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
                    OperationResult.Error("Exception: ${e.message}")
                }

            }
        } catch (e: Exception) {
            Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
            return OperationResult.Error("Exception: ${e.message}")
        }
    }

    suspend fun removeFavPlaylist(fQ: FavPlaylistQuery): OperationResult<FavTransactionResp>{
        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.removeFavPlayList(fQ)
                    Log.d("CHEEZY_NOTIFICATION",response.toString())
                    if (response.body() != null) {
                        val favTransaction: FavTransactionResp? = response.body()
                        if (favTransaction != null) {
                            Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())

                            OperationResult.Success(favTransaction) // Resume with true when successful
                        } else {
                            OperationResult.Error("Search result is null") // Resume with false when auth is null
                        }
                    } else {
                        OperationResult.Error("Network error")
                    }
                }
                catch (cancelError:CancellationException){
                    Log.e("PlaylistFavouritesManager Cancellation ERROR", cancelError.message.toString())
                    OperationResult.Error("Something Gone Wrong")
                }
                catch (e: Exception) {
                    Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
                    OperationResult.Error("Exception: ${e.message}")
                }

            }
        } catch (e: Exception) {
            Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
            return OperationResult.Error("Exception: ${e.message}")
        }
    }

    suspend fun getUserCustomFavPlaylist(fQ: FavPlaylistQuery): OperationResult<favPlaylists>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.getUserCustomFavPlaylist(fQ)
                    Log.d("CHEEZY_NOTIFICATION",response.toString())
                    if (response.body() != null) {
                        val favTransaction: favPlaylists? = response.body()
                        if (favTransaction != null) {
                            Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())
                            OperationResult.Success(favTransaction) // Resume with true when successful
                        } else {
                            OperationResult.Error("Search result is null") // Resume with false when auth is null
                        }
                    } else {
                        OperationResult.Error("Network error")
                    }
                }
                catch (cancelError:CancellationException){
                    Log.e("PlaylistFavouritesManager Cancellation ERROR", cancelError.message.toString())
                    OperationResult.Error("Something Gone Wrong")
                }
                catch (e: Exception) {
                    Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
                    OperationResult.Error("Exception: ${e.message}")
                }

            }
        } catch (e: Exception) {
            Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
            return OperationResult.Error("Exception: ${e.message}")
        }
        }

    suspend fun getUserFavPlaylist(fQ: FavPlaylistQuery): OperationResult<favPlaylists>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.getUserFavPlaylist(fQ)
                    Log.d("CHEEZY_NOTIFICATION",response.toString())
                    if (response.body() != null) {
                        val favTransaction: favPlaylists? = response.body()
                        if (favTransaction != null) {
                            Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())
                            OperationResult.Success(favTransaction) // Resume with true when successful
                        } else {
                            OperationResult.Error("Search result is null") // Resume with false when auth is null
                        }
                    } else {
                        OperationResult.Error("Network error")
                    }
                }
                catch (cancelError:CancellationException){
                    Log.e("PlaylistFavouritesManager Cancellation ERROR", cancelError.message.toString())
                    OperationResult.Error("Something Gone Wrong")
                }
                catch (e: Exception) {
                    Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
                    OperationResult.Error("Exception: ${e.message}")
                }

            }
        } catch (e: Exception) {
            Log.e("PlaylistFavouritesManager ERROR", e.message.toString())
            return OperationResult.Error("Exception: ${e.message}")
        }
    }

}