package com.example.mediapicker.imagevideopicker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.canwaimplementation.model.MediaFile
import com.example.mediapicker.imagevideopicker.data.repository.MediaRepository
import com.example.mediapicker.imagevideopicker.domain.events.MediaTabScreenEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaViewModel(
    private val repository: MediaRepository
) : ViewModel() {

    private val _mediaFlow = MutableStateFlow<Flow<PagingData<MediaFile>>>(emptyFlow())
    val mediaFlow: StateFlow<Flow<PagingData<MediaFile>>> = _mediaFlow.asStateFlow()


    private val _selectedMediaType = MutableStateFlow("Image")
    val selectedMediaType = _selectedMediaType.asStateFlow()


    private val _isLongClickListenerIsEnable = MutableStateFlow(false)
    val isLongClickListenerIsEnable = _isLongClickListenerIsEnable.asStateFlow()


    private val _selectedMediaList = MutableStateFlow(mutableListOf<MediaFile>())
    val selectedMediaList = _selectedMediaList.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getImagesMediaFiles().collectLatest { data ->
                Log.d("GalleryData", data.toString())
            }
        }
    }

    fun updateSelectedMediaType(type: String) {
        _selectedMediaType.update {
            type
        }
    }


    fun onAction(mediaTabScreenEvents: MediaTabScreenEvents) {
        when (mediaTabScreenEvents) {
            MediaTabScreenEvents.OnImageTabClick -> {
                _mediaFlow.value = repository.getImagesMediaFiles()
            }

            MediaTabScreenEvents.OnVideoTabClick -> {
                _mediaFlow.value = repository.getVideoMediaFiles()
            }
        }
    }

    fun updateLongClick(isLongClick: Boolean) {
        _isLongClickListenerIsEnable.update {
            isLongClick
        }
    }

    fun updateSelectedMediaList(media: MediaFile) {
        _selectedMediaList.update { currentList ->
            val newList = currentList.toMutableList()
            if (newList.contains(media)) {
                newList.remove(media)
            } else {
                newList.add(media)
            }
            newList
        }
    }

    fun clearSelectedMediaList() {
        _selectedMediaList.value = mutableListOf()
    }
}


