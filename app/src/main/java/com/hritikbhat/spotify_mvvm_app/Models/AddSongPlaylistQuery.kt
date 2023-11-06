package com.hritikbhat.spotify_mvvm_app.Models

data class AddSongPlaylistQuery(
    val passhash: String,
    val plid: String,
    val sid: String
)