package ru.hse.elysiumapp.data.entities

data class Song(
    val trackId: String = "",
    val name: String = "",
    val author: String = "",
    val musicUrl: String = "",
    val coverUrl: String = "",
    val streams: Int = 0,
    val mood: String = "",
    val genre: String = "",
    val comments: String = ""
)
