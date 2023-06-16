package ru.hse.elysiumapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.elysiumapp.data.entities.Song
import ru.hse.elysiumapp.exoplayer.MusicService
import ru.hse.elysiumapp.other.Resource

class SearchViewModel : ViewModel() {
    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    init {
        _mediaItems.postValue(Resource.success(emptyList()))
    }

    fun provideSearch(request: String) {
        if (request.isEmpty()) {
            _mediaItems.postValue(Resource.success(emptyList()))
        } else {
            MusicService.requestSearch(request) { songs ->
                _mediaItems.postValue(Resource.success(songs))
            }
        }
    }
}