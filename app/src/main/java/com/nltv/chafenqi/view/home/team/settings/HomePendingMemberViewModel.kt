package com.nltv.chafenqi.view.home.team.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamInfo
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.team.HomeTeamPageUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomePendingMemberUiState(
    val currentTeamId: Int? = null,
    val team: TeamInfo = TeamInfo.empty,
)

class HomePendingMemberViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomePendingMemberUiState())
    val uiState = _uiState.asStateFlow()

    val mode = CFQUser.mode
    val token = CFQUser.token
    val userId = CFQUser.userId
    val username = CFQUser.username

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTeam = CFQTeamServer.fetchCurrentTeam(CFQUser.token, mode)
            if (currentTeam == null) {
                _uiState.update {
                    it.copy(
                        currentTeamId = null,
                    )
                }
                return@launch
            }

            val teamInfo = CFQTeamServer.fetchTeamInfo(CFQUser.token, mode, currentTeam)
            if (teamInfo == null) {
                _uiState.update {
                    it.copy(
                        currentTeamId = null,
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    currentTeamId = currentTeam,
                    team = teamInfo,
                )
            }
        }
    }
}