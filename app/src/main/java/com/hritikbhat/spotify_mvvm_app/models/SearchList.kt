package com.hritikbhat.spotify_mvvm_app.models

data class SearchList(
    val artists: List<Artist>,
    val playlist: List<Playlist>,
    val song: List<Song>
)