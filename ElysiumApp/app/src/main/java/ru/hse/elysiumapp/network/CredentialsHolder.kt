package ru.hse.elysiumapp.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object CredentialsHolder {
    var token: String? = null

    val client =
        OkHttpClient.Builder().pingInterval(1, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false).build()
}