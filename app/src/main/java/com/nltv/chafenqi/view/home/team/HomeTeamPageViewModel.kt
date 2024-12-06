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
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.model.team.TeamActivity
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamBulletinBoardEntry
import com.nltv.chafenqi.model.team.TeamCourseRecord
import com.nltv.chafenqi.model.team.TeamInfo
import com.nltv.chafenqi.model.team.TeamMember
import com.nltv.chafenqi.model.team.TeamPendingMember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeTeamPageUiState(
    val team: TeamInfo = TeamInfo.sample,
)

class HomeTeamPageViewModel : ViewModel() {
    data class HomeTeamPageTab(
        val title: String,
        val icon: ImageVector,
        val iconSelected: ImageVector,
    )

    private val _uiState = MutableStateFlow(HomeTeamPageUiState())
    val uiState = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    team = TeamInfo.sample
                )
            }
        }
    }

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