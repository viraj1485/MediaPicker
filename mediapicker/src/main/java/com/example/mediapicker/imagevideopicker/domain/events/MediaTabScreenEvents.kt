package com.example.mediapicker.imagevideopicker.domain.events

sealed class MediaTabScreenEvents {
    data object OnImageTabClick : MediaTabScreenEvents()
    data object OnVideoTabClick : MediaTabScreenEvents()
}