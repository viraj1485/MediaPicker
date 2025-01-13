package com.example.mediapicker.imagevideopicker.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MediaSwitcher(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Black,
    textBackGroundColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = Color.White,
    fontSize: TextUnit = 12.sp,
    selectedTabColor: Color = Color.Green,
    unSelectedTabColor: Color = textBackGroundColor,
    selectedTab: String = "Image",
    onTabClick: (String) -> Unit
) {

    val tabs = listOf("Image", "Video")

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        tabs.forEach {
            Text(
                it, modifier = Modifier
                    .background(
                        if (selectedTab.equals(it)) selectedTabColor else unSelectedTabColor,
                        RoundedCornerShape(100.dp)
                    )
                    .padding(vertical = 5.dp, horizontal = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onTabClick(it)
                    },
                color = textColor,
                fontSize = fontSize
            )
        }
    }
}