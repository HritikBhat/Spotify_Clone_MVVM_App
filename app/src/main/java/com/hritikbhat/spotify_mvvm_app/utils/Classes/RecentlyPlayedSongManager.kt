package com.hritikbhat.spotify_mvvm_app.utils.Classes

import android.util.Log
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.SearchList
import com.hritikbhat.spotify_mvvm_app.models.SearchQuery
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class RecentlyPlayedSongManager(private val apiService: ApiService) {


    suspend fun getRecent(pashHash:String): OperationResult<List<Song>>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    val response = apiService.getUserRecent(SearchQuery(pashHash,""))
                    if (response.body() != null) {
                        val searchResult: SearchList? = response.body()
                        if (searchResult != null) {
                            OperationResult.Success(searchResult.song)
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

    suspend fun addRecentSong(fQ: FavSongQuery): OperationResult<FavTransactionResp> {

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.addRecentSong(fQ)
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
                    Log.e("RecentlyPlayedSongManager Cancellation ERROR", cancelError.message.toString())
                    OperationResult.Error("Something Gone Wrong")
                }
                catch (e: Exception) {
                    Log.e("RecentlyPlayedSongManager ERROR", e.message.toString())
                    OperationResult.Error("Exception: ${e.message}")
                }

            }
        } catch (e: Exception) {
            Log.e("RecentlyPlayedSongManager ERROR", e.message.toString())
            return OperationResult.Error("Exception: ${e.message}")
        }
    }
    


}