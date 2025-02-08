package com.nltv.chafenqi.view.home.team

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamInfo
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeTeamLeaderboardUiState(
    val teams: List<TeamBasicInfo> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeTeamLeaderboardViewModel: ViewModel() {
    data class HomeTeamLeaderboardHelpData(
        val title: String,
        val content: String,
        val icon: ImageVector,
    )
    
    private val _uiState = MutableStateFlow(HomeTeamLeaderboardUiState())
    val uiState = _uiState.asStateFlow()

    val mode = CFQUser.mode
    val token = CFQUser.token

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            val allTeams =
                CFQTeamServer
                    .fetchAllTeams(token, mode)
                    .sortedByDescending {
                        it.currentActivityPoints
                    }
            _uiState.update {
                it.copy(
                    teams = allTeams,
                    isLoading = false
                )
            }
        }
    }
    
    val helpData = listOf(
        HomeTeamLeaderboardHelpData(
            title = "游玩并上传成绩",
            content = "加入团队后，在机台上游玩任意曲目并上传，即可为团队累计积分",
            icon = Icons.AutoMirrored.Outlined.Send
        ),
        HomeTeamLeaderboardHelpData(
            title = "积分赛季",
            content = "团队积分以月份为周期进行统计，每月1号将重置积分",
            icon = Icons.Outlined.CalendarMonth
        ),
        HomeTeamLeaderboardHelpData(
            title = "赛季内活动",
            content = "赛季内将不定期举办团队积分活动，详情可参考排行榜页面",
            icon = Icons.Outlined.Event
        ),
        HomeTeamLeaderboardHelpData(
            title = "关于积分",
            content = "成功上传后，可获得的积分由本次游玩曲目数和游玩成绩相关",
            icon = Icons.Outlined.Info
        )
    )
}