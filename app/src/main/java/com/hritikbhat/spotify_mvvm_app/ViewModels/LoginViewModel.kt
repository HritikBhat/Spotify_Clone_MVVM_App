package com.hritikbhat.spotify_mvvm_app.ViewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.Models.Auth
import com.hritikbhat.spotify_mvvm_app.Models.LoginRequest
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel : ViewModel() {

    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

    fun doFirstTimeLogin(
        sharedPref: SharedPreferences,
        name: String,
        email: String,
        img_link: String
    ) {
        val editor = sharedPref.edit()

        viewModelScope.launch {
            try {
                val response = apiService.doLogin(LoginRequest(name, email, img_link))
                if (response.body()!=null){

                    val auth: Auth? = response.body()
                    if (auth != null) {
                        val passHash = auth.passhash
                        editor.putString("passHash",passHash)
                        editor.apply()

                        // Now, 'passHash' contains the passHash value from the response
                        Log.d("1st_CHEEZY_SUCCESS",passHash)
                        Log.d("1st_CHEEZY_SUCCESS2",sharedPref.getString("passHash","").toString())
                    } else {
                        // Handle the case where the response body is null
                    }
                }
                else{
                    Log.d("ERROR","SOmething went wrong")
                }
                // Handle the successful response
            } catch (e: Exception) {
                Log.d("ERROR",e.message.toString())
                // Handle any exceptions or errors
            }
        }
    }

    suspend fun autoLogin(sharedPref: SharedPreferences): Boolean = suspendCoroutine { continuation ->
        val editor = sharedPref.edit()
        viewModelScope.launch {
            try {
                val curr_passHash = sharedPref.getString("passHash", "").toString()
                Log.d("CHEEZY_NOTIFICATION",curr_passHash)
                val response = apiService.autoLogin(Auth(curr_passHash))
                if (response.body() != null) {
                    val auth: Auth? = response.body()
                    if (auth != null) {
                        Log.d("CHEEZY_NOTIFICATION",auth.passhash)
                        editor.putString("passHash", auth.passhash)
                        editor.apply()
                        continuation.resume(true) // Resume with true when successful
                    } else {
                        continuation.resume(false) // Resume with false when auth is null
                    }
                } else {
                    continuation.resume(false) // Resume with false for network errors
                }
            } catch (e: Exception) {
                Log.d("ERROR", e.message.toString())
                continuation.resume(false) // Resume with false on exception
            }
        }
    }



}