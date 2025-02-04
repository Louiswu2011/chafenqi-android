package com.nltv.chafenqi.view.home.team

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
}