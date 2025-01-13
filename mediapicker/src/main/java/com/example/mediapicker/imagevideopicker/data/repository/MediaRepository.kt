package com.example.mediapicker.imagevideopicker.data.repository

import android.content.ContentResolver
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.canwaimplementation.model.MediaFile
import com.example.canwaimplementation.paging.ImagePagingSource
import com.example.canwaimplementation.paging.VideoPagingSource
import kotlinx.coroutines.flow.Flow

class MediaRepository(private val contentResolver: ContentResolver) {
    fun getImagesMediaFiles(): Flow<PagingData<MediaFile>> {
        return Pager(
            PagingConfig(pageSize = 20)
        ) {
            ImagePagingSource(contentResolver)
        }.flow
    }

    fun getVideoMediaFiles(): Flow<PagingData<MediaFile>> {
        return Pager(
            PagingConfig(pageSize = 20)
        ) {
            VideoPagingSource(contentResolver)
        }.flow
    }
}
