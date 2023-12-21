package com.hritikbhat.spotify_mvvm_app.viewModels

import com.hritikbhat.spotify_mvvm_app.utils.Classes.LoginManager
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)


    private var loginManager = LoginManager(apiService)

    fun doFirstTimeLogin(sharedPref: SharedPreferences, name: String, email: String, imgLink: String) {
        viewModelScope.launch {
            Log.d("Status","doFirstTimeLogin started")
            loginManager.doFirstTimeLogin(sharedPref, name, email, imgLink)
        }
    }

    suspend fun checkUserLoggedIn(sharedPref: SharedPreferences): Boolean {
        return loginManager.checkUserLoggedIn(sharedPref)
    }
}