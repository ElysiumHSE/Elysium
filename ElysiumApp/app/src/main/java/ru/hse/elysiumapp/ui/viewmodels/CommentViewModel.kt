package ru.hse.elysiumapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.exoplayer.MusicService
import ru.hse.elysiumapp.exoplayer.MusicServiceConnection
import ru.hse.elysiumapp.other.Resource
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _commentItems = MutableLiveData<Resource<List<Comment>>>()
    val commentItems: LiveData<Resource<List<Comment>>> = _commentItems

    val curPlayingSong = musicServiceConnection.curPlayingSong

    fun loadComments(trackId: Int) {
        _commentItems.postValue(Resource.loading(null))
        MusicService.loadCommentsForTrack(trackId) { result ->
            _commentItems.postValue(Resource.success(result))
        }
    }
}