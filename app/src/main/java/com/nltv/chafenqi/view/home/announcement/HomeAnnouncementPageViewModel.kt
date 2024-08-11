package com.nltv.chafenqi.view.home.announcement

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.util.AppAnnouncement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeAnnouncementPageUiState(
    val announcements: List<AppAnnouncement> = emptyList()
)

class HomeAnnouncementPageViewModel : ViewModel() {
    private val tag = "HomeAnnouncementPageViewModel"

    private val _uiState = MutableStateFlow(HomeAnnouncementPageUiState())
    val uiState: StateFlow<HomeAnnouncementPageUiState> = _uiState.asStateFlow()

    suspend fun loadAnnouncements() {
        _uiState.update { currentState ->
            currentState.copy(
                announcements = CFQServer.apiFetchAnnouncement()
            )
        }
    }
}