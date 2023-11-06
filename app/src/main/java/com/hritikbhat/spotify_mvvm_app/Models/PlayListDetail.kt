package com.hritikbhat.spotify_mvvm_app.Models

import com.hritikbhat.spotify_mvvm_app.Models.Song

data class PlayListDetail(
    val songs: List<Song>,
    val isFav: Boolean,
    val pltype: Int
)