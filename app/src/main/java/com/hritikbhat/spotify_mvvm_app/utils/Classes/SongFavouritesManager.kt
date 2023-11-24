package com.hritikbhat.spotify_mvvm_app.utils.Classes

import android.util.Log
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class SongFavouritesManager(private val apiService: ApiService) {

    suspend fun addFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.addFavSong(fQ)
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
                    Log.e("SongFavouriteManager Cancellation ERROR", cancelError.message.toString())
                    OperationResult.Error("Something Gone Wrong")
                }
                catch (e: Exception) {
                    Log.e("SongFavouriteManager ERROR", e.message.toString())
                    OperationResult.Error("Exception: ${e.message}")
                }

            }
        } catch (e: Exception) {
            Log.e("SongFavouriteManager ERROR", e.message.toString())
            return OperationResult.Error("Exception: ${e.message}")
        }
    }

    suspend fun removeFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp>{
        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.removeFavSong(fQ)
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
                    Log.e("SongFavouriteManager Cancellation ERROR", cancelError.message.toString())
                    OperationResult.Error("Something Gone Wrong")
                }
                catch (e: Exception) {
                    Log.e("SongFavouriteManager ERROR", e.message.toString())
                    OperationResult.Error("Exception: ${e.message}")
                }

            }
        } catch (e: Exception) {
            Log.e("SongFavouriteManager ERROR", e.message.toString())
            return OperationResult.Error("Exception: ${e.message}")
        }
        }

    suspend fun getFavSongsDetails(plq: PlayListQuery): OperationResult<PlayListDetail>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",plq.toString())
                    val response = apiService.getUserFavouriteSongs(plq)
                    if (response.body() != null) {
                        val playListDetails: PlayListDetail? = response.body()
                        if (playListDetails != null) {
                            Log.d("CHEEZY_NOTIFICATION",playListDetails.toString())

                            OperationResult.Success(playListDetails)
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