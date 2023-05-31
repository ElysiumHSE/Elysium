package ru.hse.elysiumapp.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.data.entities.Song
import ru.hse.elysiumapp.network.CredentialsHolder
import ru.hse.elysiumapp.other.Constants
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch

class MusicDatabase {
    val client = CredentialsHolder.client

    suspend fun getAllSongs(): List<Song> {
        return try {
            val request = Request
                .Builder()
                .url(Constants.BASE_URL + "track/getAll")
                .header("Authorization", CredentialsHolder.token!!)
                .get()
                .build()
            var result = emptyList<Song>()
            val countDownLatch = CountDownLatch(1)
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    countDownLatch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    when (response.code) {
                        (HttpURLConnection.HTTP_UNAUTHORIZED) -> {
                            result = emptyList()
                            Log.println(Log.WARN, "Unauthorized", "You are suddenly unauthorized")
                        }
                        (HttpURLConnection.HTTP_INTERNAL_ERROR) -> {
                            result = emptyList()
                            Log.println(Log.ERROR, "Server Error", "Server couldn't find tracks")
                        }
                        (HttpURLConnection.HTTP_OK) -> {
                            val typeToken = object : TypeToken<List<Song>>() {}.type
                            result = Gson().fromJson(response.body!!.string(), typeToken)
                            Log.println(Log.INFO, "Got tracks", result.size.toString())
                            for (song in result) {
                                Log.println(Log.INFO, "track", Gson().toJson(song))
                            }
                        }
                    }
                    countDownLatch.countDown()
                }
            })
            withContext(Dispatchers.IO) {
                countDownLatch.await()
            }
            return result
        } catch (e: Exception) {
            emptyList()
        }
    }
}
