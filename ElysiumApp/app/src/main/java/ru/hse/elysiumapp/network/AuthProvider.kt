package ru.hse.elysiumapp.network

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import ru.hse.elysiumapp.other.Constants
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class AuthProvider {
    private val client =
        OkHttpClient.Builder().pingInterval(1, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false).build()

    fun login(username: String, password: String): LoginError {
        val jsonString = Gson().toJson(LoginPasswordForm(username, password))
        Log.println(Log.INFO, "login", jsonString)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(Constants.BASE_URL + "auth/login").post(body).build()
        var loginError = LoginError.OK
        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loginError = LoginError.CALL_FAILURE
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.println(Log.INFO, "login response message", response.message)
                if (response.code == HttpURLConnection.HTTP_OK) {
                    CredentialsHolder.token = response.message
                } else if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    loginError = LoginError.INCORRECT_DATA
                } else {
                    loginError = LoginError.UNKNOWN_RESPONSE
                }
                countDownLatch.countDown()

            }
        })
        countDownLatch.await()
        return loginError
    }

    fun register(username: String, password: String): RegisterError {
        val jsonString = Gson().toJson(LoginPasswordForm(username, password))
        Log.println(Log.INFO, "register", jsonString)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(Constants.BASE_URL + "auth/register").post(body).build()
        var registerError = RegisterError.OK
        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                registerError = RegisterError.CALL_FAILURE
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == HttpURLConnection.HTTP_BAD_REQUEST) {
                    registerError = RegisterError.USER_ALREADY_EXISTS
                } else if (response.code != HttpURLConnection.HTTP_CREATED) {
                    registerError = RegisterError.UNKNOWN_RESPONSE
                }
                countDownLatch.countDown()
            }

        })
        countDownLatch.await()
        return registerError
    }
}