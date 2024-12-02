package com.nltv.chafenqi.view.info.maimai

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.user.CFQUser

class InfoMaimaiPageViewModel : ViewModel() {
    val user = CFQUser

    val maimai = user.maimai
    val info = maimai.info
    val extra = maimai.extra

    val isProfileEmpty = maimai.isExtraEmpty

    val currentNameplate = extra.nameplates.first { it.current }
    val currentLeader = extra.characters.first { it.current }

    val currentTeam =
        if (extra.characters.isNotEmpty()) extra.characters.filter { it.current && it.url != info.lastOrNull()?.charUrl } else listOf()

    val trophyGroups = extra.trophies.groupBy { it.type }
    val characterGroups = extra.characters.groupBy { it.area }
    val frameGroups = extra.frames.groupBy { it.area }
    val nameplates = extra.nameplates
}