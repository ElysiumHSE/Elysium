package ru.hse.elysiumapp.network

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import ru.hse.elysiumapp.forms.LoginPasswordRequestForm
import ru.hse.elysiumapp.other.Constants
import java.io.IOException
import java.net.HttpURLConnection


class AuthProvider {
    private val client = CredentialsHolder.client

    fun login(username: String, password: String, callback: (LoginError) -> Unit) {
        val jsonString = Gson().toJson(LoginPasswordRequestForm(username, password))
        Log.println(Log.INFO, "login", jsonString)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(Constants.BASE_URL + "auth/login").post(body).build()
        var loginError = LoginError.OK
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loginError = LoginError.CALL_FAILURE
                callback.invoke(loginError)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    HttpURLConnection.HTTP_OK -> {
                        CredentialsHolder.token = response.body!!.string()
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        loginError = LoginError.INCORRECT_DATA
                    }
                    else -> {
                        loginError = LoginError.UNKNOWN_RESPONSE
                    }
                }
                callback.invoke(loginError)
            }
        })
    }

    fun register(username: String, password: String, callback: (RegisterError) -> Unit) {
        val jsonString = Gson().toJson(LoginPasswordRequestForm(username, password))
        Log.println(Log.INFO, "register", jsonString)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(Constants.BASE_URL + "auth/register").post(body).build()
        var registerError = RegisterError.OK
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                registerError = RegisterError.CALL_FAILURE
                callback.invoke(registerError)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == HttpURLConnection.HTTP_BAD_REQUEST) {
                    registerError = RegisterError.USER_ALREADY_EXISTS
                } else if (response.code != HttpURLConnection.HTTP_CREATED) {
                    registerError = RegisterError.UNKNOWN_RESPONSE
                }
                callback.invoke(registerError)
            }
        })
    }
}