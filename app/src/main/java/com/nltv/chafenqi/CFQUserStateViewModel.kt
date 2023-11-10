package com.nltv.chafenqi

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.CFQUser

class CFQUserStateViewModel : ViewModel() {
    val user = CFQUser
    var isLoggedIn by mutableStateOf(false)

    fun logout() {
        user.clearProfile()
        isLoggedIn = false
    }
}

val LocalUserState = compositionLocalOf<CFQUserStateViewModel> { error("User state not found.") }