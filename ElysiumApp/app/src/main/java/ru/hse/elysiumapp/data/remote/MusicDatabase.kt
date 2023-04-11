package ru.hse.elysiumapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.hse.elysiumapp.data.entities.Song
import ru.hse.elysiumapp.other.Constants.SONG_COLLECTION
import java.lang.Exception

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    suspend fun getAllSongs() : List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch(e : Exception) {
            emptyList()
        }
    }
}
