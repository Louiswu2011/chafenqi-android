package com.nltv.chafenqi.view.info.maimai

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.user.CFQUser

class InfoMaimaiPageViewModel : ViewModel() {
    val user = CFQUser

    val maimai = user.maimai
    val info = maimai.info
    val extra = maimai.extra

    val isProfileEmpty = maimai.isExtraEmpty

    val currentNameplate = extra.nameplates.first { it.selected == 1 }
    val currentLeader = extra.characters.first { it.selected == 1 }

    val currentTeam =
        if (extra.characters.isNotEmpty()) extra.characters.filter { it.selected == 1 && it.image != info.charUrl } else listOf()

    val trophyGroups = extra.trophies.groupBy { it.type }
    val characterGroups = extra.characters.groupBy { it.area }
    val frameGroups = extra.frames.groupBy { it.area }
    val nameplates = extra.nameplates
}