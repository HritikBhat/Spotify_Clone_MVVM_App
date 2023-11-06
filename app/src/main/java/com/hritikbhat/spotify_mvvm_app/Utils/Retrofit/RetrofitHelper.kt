package com.hritikbhat.spotify_mvvm_app.Utils.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    //const val BASE_URL = "http://192.168.1.104/HDB/spotify_lyte/" // Replace with your actual base URL
    const val BASE_URL = "https://www.pentago.in/HDB/spotify_lyte/"

    fun getInstance() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Create an instance of your ApiService using the Retrofit instance
    //val apiService: ApiService = retrofit.create(ApiService::class.java)
}
