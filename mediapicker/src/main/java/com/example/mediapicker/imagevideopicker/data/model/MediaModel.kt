package com.example.canwaimplementation.model

import android.net.Uri

data class MediaFile(
    val id: Long = 0L,
    val uri: Uri? = null,
    val name: String = "",
    val type: String = "",
    val thumbnailUrl: String = "",
)
