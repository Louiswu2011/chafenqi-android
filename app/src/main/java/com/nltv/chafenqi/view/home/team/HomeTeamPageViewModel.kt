package com.nltv.chafenqi.view.home.team

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Ballot
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.People
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel

class HomeTeamPageViewModel : ViewModel() {
    data class HomeTeamPageTab(
        val title: String,
        val icon: ImageVector,
        val iconSelected: ImageVector,
    )

    val tabs = listOf(
        HomeTeamPageTab(
            title = "成员",
            icon = Icons.Outlined.People,
            iconSelected = Icons.Filled.People
        ),
        HomeTeamPageTab(
            title = "动态",
            icon = Icons.Outlined.History,
            iconSelected = Icons.Filled.History
        ),
        HomeTeamPageTab(
            title = "组曲挑战",
            icon = Icons.Outlined.Ballot,
            iconSelected = Icons.Filled.Ballot
        ),
        HomeTeamPageTab(
            title = "留言板",
            icon = Icons.AutoMirrored.Outlined.Chat,
            iconSelected = Icons.AutoMirrored.Filled.Chat
        )
    )
}