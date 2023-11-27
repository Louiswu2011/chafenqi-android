package com.nltv.chafenqi.view.info

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.user.CFQUser

class InfoPageViewModel : ViewModel() {
    val user = CFQUser

    val mode = user.mode
}