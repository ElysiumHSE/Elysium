package ru.hse.elysiumapp.data.remote

import ru.hse.elysiumapp.data.entities.Song
import ru.hse.elysiumapp.network.MusicService
import java.lang.Exception

class MusicDatabase {
    //private val firestore = FirebaseFirestore.getInstance()
    //private val songCollection = firestore.collection(SONG_COLLECTION)
    private val musicService = MusicService
    suspend fun getAllSongs(): List<Song> {
        return try {
            musicService.getAllTracks()
            //songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
