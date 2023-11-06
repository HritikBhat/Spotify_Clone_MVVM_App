package com.hritikbhat.spotify_mvvm_app.ui.Activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hritikbhat.spotify_mvvm_app.Adapters.ImageAdapter
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.ViewModels.LoginViewModel
import com.hritikbhat.spotify_mvvm_app.databinding.ActivityLoginSignUpBinding
import kotlin.concurrent.fixedRateTimer


class LoginSignUpActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var googleConnectBtn: Button
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginSignUpBinding

    private val MY_PREFS_NAME: String = "MY_PREFS"

    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    private val imageList = listOf(
        R.drawable.signup_slide1_img,
        R.drawable.signup_slide2_img,
        R.drawable.signup_slide3_img
    )
    private var currentPage = 0

    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = applicationContext.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE)

        editor = sharedPref.edit()


        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_sign_up)
        viewPager2 = binding.viewPager2

        googleConnectBtn = binding.googleConnectBtn

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)




        val adapter = ImageAdapter(imageList)
        viewPager2.adapter = adapter



        // Auto-rotate the ViewPager2 every 2 seconds
        val handler = Handler(Looper.getMainLooper())
        val timer = fixedRateTimer("timer", false, 0, 6000) {
            handler.post {
                currentPage = (currentPage + 1) % imageList.size
                viewPager2.currentItem = currentPage
            }
        }




        googleConnectBtn.setOnClickListener {
            //loginViewModel.doFirstTimeLogin("Jimmy Doe", "jimmy22@gmail.com", "jimmy.jpg")
            //loginViewModel.autoLogin()
            signInGoogle()
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                loginViewModel.doFirstTimeLogin(sharedPref,account.displayName.toString(), account.email.toString(), account.photoUrl.toString())
                val intent = Intent(this , HomeActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()

            }
        }
    }

}

