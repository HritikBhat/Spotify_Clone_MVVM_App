package com.hritikbhat.spotify_mvvm_app.models

data class AllSearchItem(
    val id: Int,
    val albumId: Int,
    val name: String,
    val type: Int,
    val artistArr:List<String>,
    val isFav:Boolean
)