package ru.hse.elysiumapp.ui.network

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class AuthProvider {
    private val client = OkHttpClient.Builder()
        .pingInterval(1, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()

    fun login(username: String, password: String): ErrorCode {
        val jsonString = Gson().toJson(LoginPasswordForm(username, password))
        println(jsonString)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(CredentialsHolder.BASE_URL + "auth/login")
            .post(body)
            .build()
        var errorCode = ErrorCode.OK
        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error during call")
                errorCode = ErrorCode.CALL_FAILURE
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.message)
                if (response.code == HttpURLConnection.HTTP_OK) {
                    CredentialsHolder.updateToken(response.message)
                } else if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    errorCode = ErrorCode.LOGIN_INCORRECT_DATA
                }
                countDownLatch.countDown()

            }
        })
        countDownLatch.await()
        return errorCode
    }

    fun register(username: String, password: String): ErrorCode {
        val jsonString = Gson().toJson(LoginPasswordForm(username, password))
        println(jsonString)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(CredentialsHolder.BASE_URL + "auth/register")
            .post(body)
            .build()
        var errorCode = ErrorCode.OK
        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                errorCode = ErrorCode.CALL_FAILURE
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == HttpURLConnection.HTTP_BAD_REQUEST) {
                    errorCode = ErrorCode.REGISTER_USER_ALREADY_EXISTS
                } else if (response.code != HttpURLConnection.HTTP_CREATED) {
                    errorCode = ErrorCode.UNKNOWN_RESPONSE
                }
                countDownLatch.countDown()
            }

        })
        countDownLatch.await()
        return errorCode
    }
}