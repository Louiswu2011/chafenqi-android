package com.nltv.chafenqi.view.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeNavItem(val route: String, val displayName: String, val displayIcon: ImageVector) {
    object Home : HomeNavItem("front", "主页", Icons.Filled.Home)
    object Uploader : HomeNavItem("uploader", "传分", Icons.AutoMirrored.Filled.Send)
    object SongList : HomeNavItem("songlist", "歌曲", Icons.AutoMirrored.Filled.FormatListBulleted)

}
