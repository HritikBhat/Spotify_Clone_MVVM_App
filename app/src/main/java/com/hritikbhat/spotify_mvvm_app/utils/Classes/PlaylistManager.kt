package com.hritikbhat.spotify_mvvm_app.utils.Classes

import android.util.Log
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class PlaylistManager(private val apiService: ApiService) {

    suspend fun getPlaylistDetails(plq: PlayListQuery): OperationResult<PlayListDetail>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",plq.toString())
                    val response = apiService.getPlaylistDetails(plq)
                    Log.d("CHEEZY_NOTIFICATION",response.toString())
                    if (response.body() != null) {
                        val playListDetails: PlayListDetail? = response.body()
                        if (playListDetails != null) {
                            Log.d("CHEEZY_NOTIFICATION",playListDetails.toString())

                            OperationResult.Success(playListDetails)// Resume with true when successful
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