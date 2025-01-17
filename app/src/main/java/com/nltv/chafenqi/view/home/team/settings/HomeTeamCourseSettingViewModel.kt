package com.nltv.chafenqi.view.home.team.settings

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamUpdateCoursePayload
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeTeamCourseSettingUiState(
    val chuMusic: List<ChunithmMusicEntry> = CFQPersistentData.Chunithm.musicList,
    val maiMusic: List<MaimaiMusicEntry> = CFQPersistentData.Maimai.musicList,

    val chuSearchResult: List<ChunithmMusicEntry> = listOf(),
    val maiSearchResult: List<MaimaiMusicEntry> = listOf(),
)

class HomeTeamCourseSettingViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeTeamCourseSettingUiState())
    val uiState = _uiState.asStateFlow()

    val mode = CFQUser.mode

    var isSearchBarActive by mutableStateOf(false)
    var currentSelectedMusicSlot by mutableStateOf<Int?>(null)

    var currentTeamId by mutableIntStateOf(0)

    var courseName by mutableStateOf("")
    var courseLife by mutableIntStateOf(0)
    var courseTrack1 by mutableStateOf<TeamBasicInfo.CourseTrack?>(null)
    var courseTrack2 by mutableStateOf<TeamBasicInfo.CourseTrack?>(null)
    var courseTrack3 by mutableStateOf<TeamBasicInfo.CourseTrack?>(null)

    fun refresh() {
        viewModelScope.launch {
            val currentTeam = CFQTeamServer.fetchCurrentTeam(CFQUser.token, mode) ?: return@launch
            val teamInfo = CFQTeamServer.fetchTeamInfo(CFQUser.token, mode, currentTeam) ?: return@launch

            currentTeamId = currentTeam
            courseName = teamInfo.info.courseName
            courseLife = teamInfo.info.courseHealth
            courseTrack1 = teamInfo.info.courseTrack1
            courseTrack2 = teamInfo.info.courseTrack2
            courseTrack3 = teamInfo.info.courseTrack3
        }
    }

    fun upload(snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            try {
                val result = CFQTeamServer.adminUpdateTeamCourse(
                    authToken = CFQUser.token,
                    game = mode,
                    teamId = currentTeamId,
                    newCourse = TeamUpdateCoursePayload(
                        courseName = courseName,
                        courseHealth = courseLife,
                        courseTrack1 = courseTrack1!!,
                        courseTrack2 = courseTrack2!!,
                        courseTrack3 = courseTrack3!!
                    ),
                )

                if (result.isEmpty()) {
                    snackbarHostState.showSnackbar("更新组曲成功")
                    refresh()
                } else {
                    snackbarHostState.showSnackbar("更新组曲失败，${result}")
                }
            } catch (e: Exception) {
                Log.e("HomeTeamCourseSettingViewModel", "Failed to update course, $e")
                snackbarHostState.showSnackbar("更新组曲失败，${e.localizedMessage}")
            }
        }
    }

    fun search(mode: Int, query: String) {
        when (mode) {
            0 -> {
                _uiState.update { state ->
                    state.copy(
                        chuSearchResult = if (query.isEmpty()) {
                            listOf()
                        } else {
                            _uiState.value.chuMusic.filter {
                                it.title.contains(
                                    query,
                                    ignoreCase = true
                                ) || it.artist.contains(query, ignoreCase = true)
                            }
                        }
                    )
                }
            }

            1 -> {
                _uiState.update { state ->
                    state.copy(
                        maiSearchResult = if (query.isEmpty()) {
                            listOf()
                        } else {
                            _uiState.value.maiMusic.filter {
                                it.title.contains(
                                    query,
                                    ignoreCase = true
                                ) || it.basicInfo.artist.contains(query, ignoreCase = true)
                            }
                        }
                    )
                }
            }
        }
    }
}