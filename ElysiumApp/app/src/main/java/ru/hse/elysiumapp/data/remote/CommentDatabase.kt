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
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

class CommentDatabase {

    private val client = CredentialsHolder.client
    private val cached: MutableMap<Int, MutableList<Comment>> = HashMap()
    private val cacheList: MutableList<Int> = ArrayList()
    private val cacheLock: Lock = ReentrantLock()

    private fun addToCache(trackId: Int, comments: List<Comment>) {
        cacheLock.withLock {
            if (cacheList.contains(trackId)) return
            if (cacheList.size == 3) {
                Log.println(Log.INFO, "removed track id from comment cache", cacheList.first().toString())
                cached.remove(cacheList.first())
                cacheList.removeAt(0)
            }
            cached[trackId] = comments.toMutableList()
            cacheList.add(trackId)
            Log.println(Log.INFO, "added track id to comment cache", trackId.toString())
        }
    }

    private fun updateCache(trackId: Int, newComment: Comment?): Boolean {
        cacheLock.withLock {
            if (!cached.contains(trackId)) return false
            cached[trackId]!!.add(newComment!!)
            return true
        }
    }

    private fun loadFromCache(trackId: Int): List<Comment>? {
        cacheLock.withLock {
            if (!cached.contains(trackId)) return null
            Log.println(Log.INFO, "loaded from cache", trackId.toString())
            return cached[trackId]
        }
    }

    suspend fun loadAllComments(trackId: Int): List<Comment> {
        val cachedComments = loadFromCache(trackId)
        if (cachedComments != null) return cachedComments
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
                            Log.println(
                                Log.WARN,
                                "loadAllComments",
                                "You are suddenly unauthorized"
                            )
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
            addToCache(trackId, result)
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
        var newComment: Comment? = null
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
                        val typeToken = object : TypeToken<Comment>() {}.type
                        newComment = Gson().fromJson(response.body!!.string(), typeToken)
                        Log.d("addComment", "Success")
                        Log.d("addComment", newComment.toString())
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
        if (result == AddCommentsError.OK) {
            updateCache(trackId, newComment)
        }
        return result
    }
}