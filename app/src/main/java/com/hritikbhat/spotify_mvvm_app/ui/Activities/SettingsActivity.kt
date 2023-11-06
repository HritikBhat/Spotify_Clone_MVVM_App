package com.hritikbhat.spotify_mvvm_app.ui.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.ViewModels.SearchViewModel
import com.hritikbhat.spotify_mvvm_app.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private val handler = Handler(Looper.myLooper()!!)
    private lateinit var binding : ActivitySettingsBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var sharedPref: SharedPreferences

    private val MY_PREFS_NAME: String = "MY_PREFS"

    private lateinit var googleSignInClient : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding= DataBindingUtil.setContentView(this, R.layout.activity_settings)

        sharedPref = applicationContext.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE)

        val editor = sharedPref.edit()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.logoutBtn.setOnClickListener(View.OnClickListener {
            auth.signOut().apply {
                googleSignInClient.signOut()
                editor.remove("passHash")
                editor.apply()
                Log.d("CHEEZY_NOTIFICATION",sharedPref.getString("passHash","D").toString())

            }
            startActivity(Intent(this , LoginSignUpActivity::class.java))
            finish()
        })

    }
}