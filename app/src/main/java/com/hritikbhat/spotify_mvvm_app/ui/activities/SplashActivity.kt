package com.hritikbhat.spotify_mvvm_app.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.viewModels.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"

    private fun goToActivity(targetActivity: Class<*>) {
        val i = Intent(this@SplashActivity, targetActivity)
        startActivity(i)
        finish()
    }

    private fun getSharedPreferences():SharedPreferences{
        return applicationContext.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE)
    }


    private suspend fun userLoggedIn():Boolean{
        return loginViewModel.checkUserLoggedIn(sharedPref)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPref = getSharedPreferences()

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.viewModelScope.launch {
            delay(1500)
            if (userLoggedIn()){
                goToActivity(HomeActivity::class.java)
            }
            else{
                goToActivity(LoginSignUpActivity::class.java)
            }

        }


    }
}