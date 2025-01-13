package com.example.mediapicker.imagevideopicker.presentation.view

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.GlideSubcomposition
import com.bumptech.glide.integration.compose.RequestState
import com.example.canwaimplementation.model.MediaFile
import com.example.mediapicker.imagevideopicker.domain.events.MediaTabScreenEvents
import com.example.mediapicker.imagevideopicker.presentation.viewmodel.MediaViewModel
import com.example.mediapicker.imagevideopicker.presentation.viewmodel.MediaViewModelFactory
import com.example.mediapicker.R
import java.nio.file.WatchEvent

@Composable
fun MediaPickerScreen(
    modifier: Modifier = Modifier,
    onMediaClick: (MediaFile) -> Unit,
    onContinueButtonClick: (List<MediaFile>) -> Unit
) {
    val context = LocalContext.current

    val factory = MediaViewModelFactory(context.contentResolver)

    val mediaViewModel: MediaViewModel = viewModel(factory = factory)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                mediaViewModel.onAction(MediaTabScreenEvents.OnImageTabClick)
            }
        }
    )


    val selectedMediaType = mediaViewModel.selectedMediaType.collectAsState()
    val dataList = mediaViewModel.mediaFlow.collectAsState().value.collectAsLazyPagingItems()

    val isLongClickEnable = mediaViewModel.isLongClickListenerIsEnable.collectAsState()

    val selectedMedia by mediaViewModel.selectedMediaList.collectAsState()


    LaunchedEffect(Unit) {
        mediaViewModel.onAction(MediaTabScreenEvents.OnImageTabClick)
    }

    LaunchedEffect(selectedMedia) {
        if (selectedMedia.isEmpty()) {
            mediaViewModel.updateLongClick(false)
        }
    }
    LaunchedEffect(selectedMediaType.value) {
        mediaViewModel.clearSelectedMediaList()
        mediaViewModel.updateLongClick(false)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Request multiple permissions for Android Tiramisu (API 33) and above
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_VIDEO,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
            )
        } else {
            // Request legacy READ_EXTERNAL_STORAGE permission for older versions
            permissionLauncher.launch(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                MediaSwitcher(selectedTab = selectedMediaType.value) {
                    mediaViewModel.updateSelectedMediaType(it)
                    when (it) {
                        "Image" -> {
                            mediaViewModel.onAction(MediaTabScreenEvents.OnImageTabClick)
                        }

                        "Video" -> {
                            mediaViewModel.onAction(MediaTabScreenEvents.OnVideoTabClick)
                        }
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(dataList.itemCount) { index ->
                    dataList[index]?.let { media ->
                        MediaElement(media,
                            isLongClickEnable = isLongClickEnable.value,
                            showSelectedIcon = selectedMedia.contains(media),
                            selectedIndexText = (selectedMedia.indexOf(media) + 1)
                                .toString(),
                            onClick = { clickedMedia ->
                                onMediaClick(clickedMedia)
                            }, onLongClick = {
                                mediaViewModel.updateLongClick(true)
                                mediaViewModel.updateSelectedMediaList(it)
                            }, onMultipleMediaSelection = { selectedMedia ->
                                mediaViewModel.updateSelectedMediaList(selectedMedia)
                            })
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = selectedMedia.isNotEmpty(),
            enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(bottom = 50.dp, end = 50.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    onContinueButtonClick(selectedMedia)
                },
                containerColor = Color.Green,
            ) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "")
            }
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun MediaElement(
    media: MediaFile,
    isLongClickEnable: Boolean,
    showSelectedIcon: Boolean = false,
    selectedIndexText: String = "",
    onClick: (MediaFile) -> Unit,
    onLongClick: (MediaFile) -> Unit,
    onMultipleMediaSelection: (MediaFile) -> Unit
) {

    Box(modifier = Modifier.fillMaxWidth()) {

        Card(
            modifier = Modifier
                .combinedClickable(onClick = {
                    if (isLongClickEnable) {
                        onMultipleMediaSelection(media)
                    } else {
                        onClick(media)
                    }
                }, onLongClick = {
                    onLongClick(media)
                })
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(0.dp),
        ) {
            var isLoading by remember { mutableStateOf(true) }

            when (media.type) {
                "image" -> {
                    GlideImage(
                        model = media.uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                "video" -> {
                    Box(contentAlignment = Alignment.Center) {
                        GlideSubcomposition(
                            model = media.thumbnailUrl,
                        ) {
                            when (state) {
                                RequestState.Failure -> {

                                }

                                RequestState.Loading -> {

                                }

                                is RequestState.Success -> {
                                    isLoading = false
                                    painter.let {
                                        Image(
                                            painter = it,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                        this@Card.AnimatedVisibility(
                            !isLoading, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .paint(painter = painterResource(R.drawable.ic_ellipse)),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showSelectedIcon) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .background(Color.Green, CircleShape)
                    .size(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = selectedIndexText,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}