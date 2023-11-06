package com.hritikbhat.spotify_mvvm_app.Models

data class SearchList(
    val artists: List<Artist>,
    val playlist: List<Playlist>,
    val song: List<Song>
)