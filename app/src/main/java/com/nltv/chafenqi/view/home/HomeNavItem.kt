package com.nltv.chafenqi.view.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeNavItem(val route: String, val displayName: String, val displayIcon: ImageVector) {
    object Home : HomeNavItem("front", "主页", Icons.Filled.Home)
    object Uploader : HomeNavItem("uploader", "传分", Icons.Filled.Send)
    object SongList : HomeNavItem("songlist", "歌曲", Icons.Filled.FormatListBulleted)

}
