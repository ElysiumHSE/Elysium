package ru.hse.elysiumapp.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.forms.CommentRequestForm
import ru.hse.elysiumapp.network.AddCommentsError
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
                .scheme(Constants.SCHEME)
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
                            Log.println(Log.WARN, "loadAllComments", "You are suddenly unauthorized")
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
                            for (comment in result) {
                                Log.println(Log.INFO, "comment", Gson().toJson(comment))
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

    suspend fun addComment(trackId: Int, content: String): AddCommentsError {
        val jsonString = Gson().toJson(CommentRequestForm(trackId, content))
        Log.println(Log.INFO, "addComment", jsonString)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(Constants.BASE_URL + "comment/addComment")
            .post(body)
            .addHeader("Authorization", CredentialsHolder.token!!)
            .build()
        var result = AddCommentsError.OK
        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("addComment", "Call Failure")
                result = AddCommentsError.CALL_FAILURE
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    HttpURLConnection.HTTP_OK -> {
                        Log.d("addComment", "Success")
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        Log.d("addComment", "Unauthorized")
                        result = AddCommentsError.UNAUTHORIZED
                    }
                    else -> {
                        Log.d("addComment", "Unknown Response")
                        result = AddCommentsError.UNKNOWN_RESPONSE
                    }
                }
                countDownLatch.countDown()
            }
        })
        withContext(Dispatchers.IO) {
            countDownLatch.await()
        }
        return result
    }
}