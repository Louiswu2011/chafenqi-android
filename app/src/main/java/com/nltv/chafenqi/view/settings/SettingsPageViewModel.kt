package com.nltv.chafenqi.view.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.CFQUser
import kotlinx.coroutines.launch

class SettingsPageViewModel : ViewModel() {
    var showLogoutAlert by mutableStateOf(false)
    var showReloadListAlert by mutableStateOf(false)

    var isReloadingList by mutableStateOf(false)
    var isLoadingSponsorList by mutableStateOf(false)

    var sponsorList = listOf<String>()

    val username = CFQUser.username
    val token = CFQUser.token

    fun fetchSponsorList() {
        isLoadingSponsorList = true
        viewModelScope.launch {
            sponsorList = CFQServer.apiSponsorList()
            isLoadingSponsorList = false
        }
    }
}