package ru.hse.elysiumapp.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import ru.hse.elysiumapp.data.remote.MusicDatabase
import ru.hse.elysiumapp.exoplayer.State.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.data.remote.CommentDatabase
import javax.inject.Inject

class MusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase,
    private val commentDatabase: CommentDatabase
) {
    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = STATE_INITIALIZING
        val allSongs = musicDatabase.getAllSongs()
        songs = allSongs.map { song ->
            Builder()
                .putString(METADATA_KEY_ARTIST, song.author)
                .putString(METADATA_KEY_MEDIA_ID, song.trackId)
                .putString(METADATA_KEY_TITLE, song.name)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.name)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.coverUrl)
                .putString(METADATA_KEY_MEDIA_URI, song.musicUrl)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.coverUrl)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.author)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.author)
                .build()
        }
        state = STATE_INITIALIZED
    }

    suspend fun fetchCommentData(trackId: Int, applyComments: (List<Comment>) -> Unit) = withContext(Dispatchers.IO) {
        val allComments = commentDatabase.loadAllComments(trackId)
        applyComments(allComments)
    }

    suspend fun uploadComment(trackId: Int, content: String) {
        commentDatabase.addComment(trackId, content)
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(song.getString(METADATA_KEY_MEDIA_URI)))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}