package com.nltv.chafenqi.view.home.team

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Ballot
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.SnackbarHostState
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
    data class HomeTeamHelpData(
        val title: String,
        val content: String,
        val icon: ImageVector,
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

    fun getDifficultyColor(courseTrack: TeamBasicInfo.CourseTrack): Color {
        return when (mode) {
            0 -> chunithmDifficultyColors[courseTrack.levelIndex]
            1 -> maimaiDifficultyColors[courseTrack.levelIndex]
            else -> Color.Transparent
        }
    }

    fun getDifficultyColorLong(courseTrack: TeamBasicInfo.CourseTrack): Long {
        return when (mode) {
            0 -> chunithmDifficultyColors[courseTrack.levelIndex].value.toLong()
            1 -> maimaiDifficultyColors[courseTrack.levelIndex].value.toLong()
            else -> Color.Transparent.value.toLong()
        }
    }

    fun deleteBulletinBoardEntry(id: Int, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            val result = CFQTeamServer.deleteTeamBulletinBoardEntry(token, mode, _uiState.value.team.info.id, id)
            if (result.isEmpty()) {
                refresh()
            } else {
                snackbarHostState.showSnackbar("删除失败，$result")
            }
        }
    }

    fun pinBulletinBoardEntry(id: Int, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            val result = CFQTeamServer.adminSetPinnedMessage(token, mode, _uiState.value.team.info.id, id)
            if (result) {
                refresh()
            } else {
                snackbarHostState.showSnackbar("置顶失败，请联系开发者")
            }
        }
    }

    fun unpinBulletinBoardEntry(snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            val result =
                CFQTeamServer.adminResetPinnedMessage(token, mode, _uiState.value.team.info.id)
            if (result) {
                refresh()
            } else {
                snackbarHostState.showSnackbar("取消置顶失败，请联系开发者")
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
    
    val helpData = listOf(
        HomeTeamHelpData(
            title = "团队信息",
            content = "位于团队页面上方的团队信息区域，点击任意项目可以显示该项详细信息",
            icon = Icons.Outlined.Info
        ),
        HomeTeamHelpData(
            title = "成员列表",
            content = "显示当前团队成员信息，按照加入时间排序，点击任意成员可显示成员详细信息",
            icon = Icons.Outlined.People
        ),
        HomeTeamHelpData(
            title = "团队动态列表",
            content = "显示团队动态，包括成员变动，团队信息变动及组曲挑战变动等",
            icon = Icons.Outlined.History
        ),
        HomeTeamHelpData(
            title = "组曲挑战",
            content = "显示当前团队的组曲挑战，在1PC内按顺序连续游玩指定谱面后上传成绩，即可参与组曲挑战",
            icon = Icons.Outlined.Ballot
        ),
        HomeTeamHelpData(
            title = "留言板",
            content = "显示团队留言板，长按以管理自己的留言，队长可长按管理任意留言",
            icon = Icons.AutoMirrored.Outlined.Chat
        ),
        HomeTeamHelpData(
            title = "更多",
            content = "当前团队人数上限为20人，团队在订阅会员过期后仍可正常使用，但无法变更团队内的成员",
            icon = Icons.Outlined.MoreHoriz
        ),
    )
}