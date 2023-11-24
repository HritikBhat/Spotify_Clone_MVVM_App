package com.hritikbhat.spotify_mvvm_app.utils.Classes

import android.util.Log

import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class AlbumFavouriteManager(private val apiService: ApiService) {


    suspend fun getUserFavAlbum(fQ: FavPlaylistQuery): OperationResult<favPlaylists>{

        try {
            return withContext(Dispatchers.Default){
                try {
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION",fQ.toString())
                    val response = apiService.getUserFavAlbum(fQ)
                    Log.d("CHEEZY_NOTIFICATION",response.toString())
                    if (response.body() != null) {
                        val favTransaction: favPlaylists? = response.body()
                        if (favTransaction != null) {
                            Log.d("CHEEZY_NOTIFICATION",favTransaction.toString())

                            OperationResult.Success(favTransaction) // Resume with true when successful
                        }else {
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