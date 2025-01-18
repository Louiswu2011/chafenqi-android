package com.nltv.chafenqi.view.home.team

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeTeamIntroductionUiState(
    val teams: List<TeamBasicInfo> = emptyList(),
    val searchedTeam: TeamBasicInfo? = null
)

class HomeTeamIntroductionViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeTeamIntroductionUiState())
    val uiState = _uiState.asStateFlow()

    val user = CFQUser

    fun refresh() {
        viewModelScope.launch {
            val teams = CFQTeamServer.fetchAllTeams(user.token, user.mode)
            _uiState.update {
                it.copy(
                    teams = teams
                )
            }
        }
    }

    fun onSearch(teamCode: String) {
        viewModelScope.launch {
            Log.i(HomeTeamIntroductionViewModel::class.simpleName, "Searching code $teamCode")
            val team = _uiState.value.teams.firstOrNull { it.teamCode == teamCode }
            _uiState.update {
                it.copy(
                    searchedTeam = team
                )
            }
        }
    }

    fun onCancelSearch() {
        if (_uiState.value.searchedTeam == null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchedTeam = null
                )
            }
        }
    }

    fun onApply(teamId: Int, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            val result = CFQTeamServer.applyForTeam(user.token, user.mode, teamId, "")
            if (result.isEmpty()) {
                snackbarHostState.showSnackbar("申请加入成功，请等待队长审核")
            } else {
                snackbarHostState.showSnackbar("申请加入失败，$result")
            }
        }
    }
}