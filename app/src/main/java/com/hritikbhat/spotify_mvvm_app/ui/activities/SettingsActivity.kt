package com.hritikbhat.spotify_mvvm_app.ui.activities

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.SharedPreferenceInstance
import com.hritikbhat.spotify_mvvm_app.databinding.ActivitySettingsBinding
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.ui.Fragments.BaseFragment.NowPlaying

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var sharedPref: SharedPreferences

    private lateinit var googleSignInClient : GoogleSignInClient

    fun exitApplication() {
        if (PlayActivity.mediaPlayerService != null) {
            //PlayActivity.mediaPlayerService !!.audioManager.abandonAudioFocus(PlayActivity.mediaPlayerService
            PlayActivity.updateSeekBarJob!!.cancel()
            PlayActivity.mediaPlayerService!!.mediaPlayer!!.stop()
            PlayActivity.mediaPlayerService!!.mediaPlayer!!.release()
            PlayActivity.mediaPlayerService!!.mediaPlayer = null
            PlayActivity.mediaPlayerService!!.stopForeground(Service.STOP_FOREGROUND_REMOVE)
            NowPlaying.binding.root.visibility = View.GONE

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding= DataBindingUtil.setContentView(this, R.layout.activity_settings)

        sharedPref = SharedPreferenceInstance(this).getSPInstance()

        val editor = sharedPref.edit()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.logoutBtn.setOnClickListener{
            auth.signOut().apply {
                exitApplication()
                googleSignInClient.signOut()
                editor.remove("passHash")
                editor.apply()
                Log.d("CHEEZY_NOTIFICATION",sharedPref.getString("passHash","D").toString())

            }
            startActivity(Intent(this , LoginSignUpActivity::class.java))
            finish()
        }

    }
}