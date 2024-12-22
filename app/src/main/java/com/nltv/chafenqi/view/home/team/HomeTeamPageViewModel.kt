package com.nltv.chafenqi.view.home.team

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Ballot
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamInfo
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.chunithmDifficultyTitles
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyTitles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeTeamPageUiState(
    val currentTeamId: Int? = null,
    val team: TeamInfo = TeamInfo.empty,
    val isTeamAdmin: Boolean = false,
    val searchResult: List<TeamBasicInfo>? = null,
    val isLoading: Boolean = true,
)

class HomeTeamPageViewModel : ViewModel() {
    data class HomeTeamPageTab(
        val title: String,
        val icon: ImageVector,
        val iconSelected: ImageVector,
    )

    private val _uiState = MutableStateFlow(HomeTeamPageUiState())
    val uiState = _uiState.asStateFlow()

    val mode = CFQUser.mode
    val token = CFQUser.token
    val userId = CFQUser.userId
    val username = CFQUser.username

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            val currentTeam = CFQTeamServer.fetchCurrentTeam(CFQUser.token, mode)
            if (currentTeam == null) {
                _uiState.update {
                    it.copy(
                        currentTeamId = null,
                        isLoading = false
                    )
                }
                return@launch
            }

            val teamInfo = CFQTeamServer.fetchTeamInfo(CFQUser.token, mode, currentTeam)
            if (teamInfo == null) {
                _uiState.update {
                    it.copy(
                        currentTeamId = null,
                        isLoading = false
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    currentTeamId = currentTeam,
                    team = teamInfo,
                    isTeamAdmin = teamInfo.info.leaderUserId == userId,
                    isLoading = false
                )
            }
        }
    }

    fun updateBulletinBoard() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTeam = CFQTeamServer.fetchCurrentTeam(CFQUser.token, mode)
            if (currentTeam == null) return@launch

            val entries = CFQTeamServer.getTeamBulletinBoard(
                authToken = token,
                game = mode,
                teamId = currentTeam
            )

            if (entries == null) return@launch
            _uiState.update {
                it.copy(
                    team = it.team.copy(
                        bulletinBoard = entries
                    )
                )
            }
        }
    }

    fun getCoverPath(courseTrack: TeamBasicInfo.CourseTrack): String {
       return when (mode) {
            0 -> { CFQPersistentData.Chunithm.musicList.firstOrNull { it.musicId == courseTrack.musicId.toInt() }?.musicId?.toChunithmCoverPath() ?: return "" }
            1 -> { CFQPersistentData.Maimai.musicList.firstOrNull { it.musicId == courseTrack.musicId.toInt() }?.coverId?.toMaimaiCoverPath() ?: return "" }
            else -> ""
        }
    }

    fun getTitle(courseTrack: TeamBasicInfo.CourseTrack): String {
        return when (mode) {
            0 -> CFQPersistentData.Chunithm.musicList.firstOrNull { it.musicId == courseTrack.musicId.toInt() }?.title?: ""
            1 -> CFQPersistentData.Maimai.musicList.firstOrNull { it.musicId == courseTrack.musicId.toInt() }?.title?: ""
            else -> ""
        }
    }

    fun getArtist(courseTrack: TeamBasicInfo.CourseTrack): String {
        return when (mode) {
            0 -> CFQPersistentData.Chunithm.musicList.firstOrNull { it.musicId == courseTrack.musicId.toInt() }?.artist?: ""
            1 -> CFQPersistentData.Maimai.musicList.firstOrNull { it.musicId == courseTrack.musicId.toInt() }?.basicInfo?.artist ?: ""
            else -> ""
        }
    }

    fun getDifficultyString(courseTrack: TeamBasicInfo.CourseTrack): String {
        return when (mode) {
            0 -> chunithmDifficultyTitles[courseTrack.levelIndex]
            1 -> maimaiDifficultyTitles[courseTrack.levelIndex]
            else -> ""
        }
    }

    fun getDifficultyColor(courseTrack: TeamBasicInfo.CourseTrack): Long {
        return when (mode) {
            0 -> chunithmDifficultyColors[courseTrack.levelIndex].value.toLong()
            1 -> maimaiDifficultyColors[courseTrack.levelIndex].value.toLong()
            else -> Color.Transparent.value.toLong()
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

    val introTabs = listOf(
        HomeTeamPageTab(
            title = "加入团队",
            icon = Icons.Outlined.Search,
            iconSelected = Icons.Filled.Search
        ),
        HomeTeamPageTab(
            title = "创建团队",
            icon = Icons.Outlined.GroupAdd,
            iconSelected = Icons.Filled.GroupAdd
        )
    )
}