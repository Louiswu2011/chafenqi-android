package com.nltv.chafenqi.view.info.maimai

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.CFQUser

class InfoMaimaiPageViewModel : ViewModel() {
    val user = CFQUser

    val maimai = user.maimai
    val info = maimai.info
    val extra = maimai.extra

    val currentNameplate = extra.nameplates.first { it.selected == 1 }
    val currentLeader = extra.characters.first { it.selected == 1 }

    val currentTeam = extra.characters.filter { it.selected == 1 && it.image != info.charUrl }

    val trophyGroups = extra.trophies.groupBy { it.type }
    val characterGroups = extra.characters.groupBy { it.area }
    val frameGroups = extra.frames.groupBy { it.area }
    val nameplates = extra.nameplates
}