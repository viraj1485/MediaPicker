package com.example.canwaimplementation.paging

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.example.canwaimplementation.model.MediaFile


class VideoPagingSource(private val contentResolver: ContentResolver) :
    PagingSource<Int, MediaFile>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaFile> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val offset = page * pageSize

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Thumbnails.DATA
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val mediaFiles = mutableListOf<MediaFile>()
        var cursor: Cursor? = null

        try {
            // Query MediaStore without LIMIT/OFFSET and handle pagination manually
            cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                val thumbnail = it.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)

                // Debugging: log the result of moveToPosition
                val moveToPositionResult = it.moveToPosition(offset)
                if (!moveToPositionResult) {
                    // No items found for the current page
                    return LoadResult.Page(
                        data = mediaFiles,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = null
                    )
                }

                // Load the data for the current page
                var count = 0
                while (it.moveToNext() && count < pageSize) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val mimeType = it.getString(mimeTypeColumn)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    val thumbNail = it.getString(thumbnail)
                    // Get the thumbnail for image or video
                    val thumbnailUri = when {
                        mimeType.startsWith("video") -> {
                            getThumbnail(contentResolver, id, false) // Get video thumbnail
                        }
                        else -> null
                    }

                    Log.d("ThumbNailUrl",thumbNail.toString())

                    mediaFiles.add(
                        MediaFile(
                            id = id,
                            uri = contentUri,
                            name = name,
                            thumbnailUrl = thumbNail.toString(),
                            type = "video"
                        )
                    )

                    count++
                }
            }

            return LoadResult.Page(
                data = mediaFiles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (mediaFiles.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        } finally {
            cursor?.close()
        }
    }

    private fun getThumbnail(
        contentResolver: ContentResolver,
        mediaId: Long,
        isImage: Boolean
    ): Uri? {
        return if (isImage) {
            // Get image thumbnail from MediaStore.Images.Thumbnails
            val thumbnailUri = ContentUris.withAppendedId(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                mediaId
            )
            thumbnailUri
        } else {
            // Get video thumbnail from MediaStore.Video.Thumbnails
            val thumbnailUri = ContentUris.withAppendedId(
                MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                mediaId
            )
            thumbnailUri
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MediaFile>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}