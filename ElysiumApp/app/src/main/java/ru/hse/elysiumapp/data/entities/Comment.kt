package ru.hse.elysiumapp.data.entities

import java.sql.Timestamp

data class Comment(
    val trackId: Int? = null,
    val commentId: Int? = null,
    val username: String? = null,
    val content: String? = null,
    val publicationDatetime: Timestamp? = null
)
