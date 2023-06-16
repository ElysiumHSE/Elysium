package ru.hse.elysiumapp.data.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Comment(
    val trackId: Int? = null,
    val commentId: Int? = null,
    val username: String? = null,
    val content: String? = null,

    @SerializedName("time")
    val publicationDatetime: Timestamp? = null
)
