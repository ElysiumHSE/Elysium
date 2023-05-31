package ru.hse.elysiumapp.data.entities

import java.sql.Timestamp

data class Comment(
    val nickname: String? = null,
    val publicationDatetime: Timestamp? = null,
    val text: String? = null
)
