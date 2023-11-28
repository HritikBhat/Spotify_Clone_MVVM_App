package com.hritikbhat.spotify_mvvm_app.viewModels.SubFragmentsViewModels

import androidx.lifecycle.ViewModel
import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.OperationResult
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import com.hritikbhat.spotify_mvvm_app.utils.Classes.AlbumFavouriteManager
import com.hritikbhat.spotify_mvvm_app.utils.Classes.CustomPlaylistSongManager
import com.hritikbhat.spotify_mvvm_app.utils.Classes.PlaylistFavouritesManager
import com.hritikbhat.spotify_mvvm_app.utils.Classes.PlaylistManager
import com.hritikbhat.spotify_mvvm_app.utils.Classes.SongFavouritesManager
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper

class FavAlbumViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

    private var songFavouritesManager = SongFavouritesManager(apiService)
    private var playlistFavouritesManager = PlaylistFavouritesManager(apiService)
    private var customPlaylistSongManager = CustomPlaylistSongManager(apiService)
    private var playlistManager = PlaylistManager(apiService)
    private var albumFavouriteManager = AlbumFavouriteManager(apiService)

    suspend fun addFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp>{
        return songFavouritesManager.addFavSong(fQ)
    }

    suspend fun removeFavSong(fQ: FavSongQuery): OperationResult<FavTransactionResp>{
        return songFavouritesManager.removeFavSong(fQ)
    }

    suspend fun addFavPlaylist(fQ: FavPlaylistQuery): OperationResult<FavTransactionResp>{
        return playlistFavouritesManager.addFavPlaylist(fQ)
    }

    suspend fun removeFavPlaylist(fQ: FavPlaylistQuery): OperationResult<FavTransactionResp>{
        return playlistFavouritesManager.removeFavPlaylist(fQ)
    }

    suspend fun getUserCustomFavPlaylist(fQ: FavPlaylistQuery): OperationResult<favPlaylists>{
        return playlistFavouritesManager.getUserCustomFavPlaylist(fQ)
    }


    suspend fun getUserFavAlbum(fQ: FavPlaylistQuery): OperationResult<favPlaylists>{
        return albumFavouriteManager.getUserFavAlbum(fQ)
    }

    suspend fun getPlaylistDetails(plq: PlayListQuery): OperationResult<PlayListDetail> {
        return playlistManager.getPlaylistDetails(plq)
    }

    suspend fun addSongToPlaylist(fQ: AddSongPlaylistQuery): OperationResult<FavTransactionResp>{
        return customPlaylistSongManager.addSongToPlaylist(fQ)
    }


}