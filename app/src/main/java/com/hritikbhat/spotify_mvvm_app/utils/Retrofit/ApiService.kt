package com.hritikbhat.spotify_mvvm_app.utils.Retrofit

import com.hritikbhat.spotify_mvvm_app.models.AddSongPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.Auth
import com.hritikbhat.spotify_mvvm_app.models.CustomPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistQuery
import com.hritikbhat.spotify_mvvm_app.models.FavSongQuery
import com.hritikbhat.spotify_mvvm_app.models.FavTransactionResp
import com.hritikbhat.spotify_mvvm_app.models.LoginRequest
import com.hritikbhat.spotify_mvvm_app.models.PlayListQuery
import com.hritikbhat.spotify_mvvm_app.models.SearchList
import com.hritikbhat.spotify_mvvm_app.models.SearchQuery
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.models.favPlaylists
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/user/doLogin.php") // Replace with the actual URL of your PHP script
    suspend fun doLogin(@Body request: LoginRequest): Response<Auth>

    @POST("api/user/autoLogin.php") // Replace with the actual URL of your PHP script
    suspend fun checkUserLoggedIn(@Body request: Auth): Response<Auth>

    @POST("api/search/searchFunc.php") // Replace with the actual URL of your PHP script
    suspend fun searchResults(@Body request: SearchQuery): Response<SearchList>

    @POST("api/playlist/getPlaylistDetails.php") // Replace with the actual URL of your PHP script
    suspend fun getPlaylistDetails(@Body request: PlayListQuery): Response<PlayListDetail>

    @POST("api/user/addFavSong.php") // Replace with the actual URL of your PHP script
    suspend fun addFavSong(@Body request: FavSongQuery): Response<FavTransactionResp>

    @POST("api/user/removeFavSong.php") // Replace with the actual URL of your PHP script
    suspend fun removeFavSong(@Body request: FavSongQuery): Response<FavTransactionResp>

    @POST("api/user/addFavPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun addFavPlayList(@Body request: FavPlaylistQuery): Response<FavTransactionResp>

    @POST("api/user/removeFavPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun removeFavPlayList(@Body request: FavPlaylistQuery): Response<FavTransactionResp>

    @POST("api/user/getUserFavPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun getUserFavPlaylist(@Body request: FavPlaylistQuery): Response<favPlaylists>

    @POST("api/user/getUserFavAlbum.php") // Replace with the actual URL of your PHP script
    suspend fun getUserFavAlbum(@Body request: FavPlaylistQuery): Response<favPlaylists>

    @POST("api/user/getUserFavPodcast.php") // Replace with the actual URL of your PHP script
    suspend fun getUserFavPodcast(@Body request: FavPlaylistQuery): Response<favPlaylists>

    @POST("api/user/addRecent.php") // Replace with the actual URL of your PHP script
    suspend fun addRecentSong(@Body request: FavSongQuery): Response<FavTransactionResp>

    @POST("api/user/getUserRecent.php") // Replace with the actual URL of your PHP script
    suspend fun getUserRecent(@Body request: SearchQuery): Response<SearchList>

    @POST("api/user/getUserFavouriteSongs.php") // Replace with the actual URL of your PHP script
    suspend fun getUserFavouriteSongs(@Body request: PlayListQuery): Response<PlayListDetail>

    @POST("api/user/addCustomPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun addCustomPlaylist(@Body request: CustomPlaylistQuery): Response<FavTransactionResp>

    @POST("api/user/getUserCustomFavPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun getUserCustomFavPlaylist(@Body request: FavPlaylistQuery): Response<favPlaylists>

    @POST("api/user/addSongToPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun addSongToPlaylist(@Body request: AddSongPlaylistQuery): Response<FavTransactionResp>

    @POST("api/user/deleteCustomPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun deleteCustomPlaylist(@Body request: AddSongPlaylistQuery): Response<FavTransactionResp>

    @POST("api/user/removeSongFromPlaylist.php") // Replace with the actual URL of your PHP script
    suspend fun removeSongFromPlaylist(@Body request: AddSongPlaylistQuery): Response<FavTransactionResp>


}