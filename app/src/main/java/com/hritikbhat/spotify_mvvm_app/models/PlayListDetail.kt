package com.hritikbhat.spotify_mvvm_app.models

data class PlayListDetail(
    val songs: List<Song>,
    val isFav: Boolean,
    val pltype: Int
)