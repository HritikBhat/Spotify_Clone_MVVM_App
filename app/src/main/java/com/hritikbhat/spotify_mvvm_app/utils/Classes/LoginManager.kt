package com.hritikbhat.spotify_mvvm_app.utils.Classes

import android.content.SharedPreferences
import android.util.Log
import com.hritikbhat.spotify_mvvm_app.models.Auth
import com.hritikbhat.spotify_mvvm_app.models.LoginRequest
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class LoginManager(private val apiService: ApiService) {

    //When user logs in initially just after installing app
    suspend fun doFirstTimeLogin(sharedPref: SharedPreferences,
                                 name: String, email: String, imgLink: String
    ) {
        val editor = sharedPref.edit()

        try {
            withContext(Dispatchers.Default) {
                try {
                    ensureActive()
                    val response = apiService.doLogin(LoginRequest(name, email, imgLink))
                    if (response.body() != null) {

                        val auth: Auth? = response.body()
                        if (auth != null) {

                            withContext(Dispatchers.IO) {
                                val passHash = auth.passhash
                                editor.putString("passHash", passHash)
                                editor.apply()
                            }
                        } else {

                            Log.d("Status","doFirstTimeLogin having some issues")
                            Log.d("Response", "${response.body()}")
                            // Handle the case where the response body is null
                        }
                    } else {
                        Log.d("ERROR", "SOmething went wrong")
                    }
                    // Handle the successful response
                } catch (e: Exception) {
                    Log.d("ERROR", e.message.toString())
                    // Handle any exceptions or errors
                }
            }
        }catch (cancelError: CancellationException){
            Log.d("SCOPE ERROR", cancelError.message.toString())
        }

        }

    //Checks whether loggedIn User is valid or not
    suspend fun checkUserLoggedIn(sharedPref: SharedPreferences): Boolean{

        val editor = sharedPref.edit()
        val currPassHash = sharedPref.getString("passHash", "").toString()

        try {
            return withContext(Dispatchers.Default){
                try {
                    val userLoggedInStatus: Boolean
                    ensureActive()
                    Log.d("CHEEZY_NOTIFICATION", currPassHash)
                    val response = apiService.checkUserLoggedIn(Auth(currPassHash))
                    userLoggedInStatus = if (response.body() != null) {
                        val auth: Auth? = response.body()
                        if (auth != null) {
                            withContext(Dispatchers.IO) {
                                Log.d("CHEEZY_NOTIFICATION", auth.passhash)
                                editor.putString("passHash", auth.passhash)
                                editor.apply()
                            }
                            true
                        } else {
                            false // Resume with false when auth is null
                        }
                    } else {
                        false
                    }
                    userLoggedInStatus
                }
                catch (e:Exception){
                    e.printStackTrace()
                    false
                }
            }
        }
        catch (cancelError: CancellationException){
            cancelError.printStackTrace()
            return false
        }
    }

}