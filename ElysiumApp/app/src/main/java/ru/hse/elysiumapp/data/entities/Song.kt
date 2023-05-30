package ru.hse.elysiumapp.data.entities

data class Song(
    val trackId: String = "",
    val name: String = "",
    val author: String = "",
    val genre : String = "",
    val mood : String = "",
    val musicUrl: String = "",
    val coverUrl: String = "",
    val streams : Int = 0,
    val comments : String = ""
)
