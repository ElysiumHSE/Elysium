package ru.hse.elysiumapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.exoplayer.MusicService
import ru.hse.elysiumapp.other.Resource

class CommentViewModel : ViewModel() {

    private val _commentItems = MutableLiveData<Resource<List<Comment>>>()
    val commentItems: LiveData<Resource<List<Comment>>> = _commentItems

    fun loadComments(trackId: Int) {
        _commentItems.postValue(Resource.loading(null))
        MusicService.loadCommentsForTrack(trackId) { result ->
            _commentItems.postValue(Resource.success(result))
        }
    }
}