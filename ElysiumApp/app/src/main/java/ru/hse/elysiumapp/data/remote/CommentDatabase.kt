package ru.hse.elysiumapp.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.network.CredentialsHolder
import ru.hse.elysiumapp.other.Constants
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch

class CommentDatabase {

    private val client = CredentialsHolder.client

    suspend fun loadAllComments(trackId: Int): List<Comment> {
        return try {
            val url = HttpUrl.Builder()
                .scheme("http")
                .host(Constants.HOST)
                .port(Constants.PORT)
                .addPathSegment("elysium")
                .addPathSegment("comment")
                .addPathSegments("loadAllComments")
                .addQueryParameter("trackId", trackId.toString())
                .build()
            val request = Request
                .Builder()
                .url(url)
                .header("Authorization", CredentialsHolder.token!!)
                .get()
                .build()
            var result = emptyList<Comment>()
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
                        (HttpURLConnection.HTTP_NO_CONTENT) -> {
                            result = emptyList()
                            Log.println(
                                Log.ERROR,
                                "No more comments",
                                "Already loaded all comments"
                            )
                        }
                        (HttpURLConnection.HTTP_OK) -> {
                            val typeToken = object : TypeToken<List<Comment>>() {}.type
                            result = Gson().fromJson(response.body!!.string(), typeToken)
                            Log.println(Log.INFO, "Got comments", result.size.toString())
                            for (song in result) {
                                Log.println(Log.INFO, "comment", Gson().toJson(song))
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