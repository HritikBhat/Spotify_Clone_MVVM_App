package com.hritikbhat.spotify_mvvm_app.utils.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    const val BASE_URL = ""

    fun getInstance() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Create an instance of your ApiService using the Retrofit instance
    //val apiService: ApiService = retrofit.create(ApiService::class.java)
}
