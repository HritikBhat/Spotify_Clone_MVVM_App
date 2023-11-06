package com.hritikbhat.spotify_mvvm_app.ui.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.ViewModels.LoginViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"

    private fun intializeActivity(targetActivity: Class<*>) {
        val i = Intent(this@SplashActivity, targetActivity)
        startActivity(i)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPref = applicationContext.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        Handler(Looper.myLooper()!!).postDelayed({ // This method will be executed once the timer is over

            GlobalScope.launch {
                if(loginViewModel.autoLogin(sharedPref)){
                    intializeActivity(HomeActivity::class.java)
                }
                else{
                    intializeActivity(LoginSignUpActivity::class.java)
                }
            }



        }, 1500)
    }
}