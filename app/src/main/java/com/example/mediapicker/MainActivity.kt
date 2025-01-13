package com.example.mediapicker

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.mediapicker.imagevideopicker.presentation.view.MediaPickerScreen
import com.example.mediapicker.ui.theme.MediaPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaPickerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MediaPickerScreen(
                        modifier = Modifier.padding(innerPadding),
                        onMediaClick = { media ->
                            Log.d("njknjgndsgfg", media.type)
                        }, onContinueButtonClick = { selectedMedia ->
                            selectedMedia.forEach {
                                Log.d("njknjgndsgfg", it.type)
                            }
                        }
                    )
                }
            }
        }
    }

}
