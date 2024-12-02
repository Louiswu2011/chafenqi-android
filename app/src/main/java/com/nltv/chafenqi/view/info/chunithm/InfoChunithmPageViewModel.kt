package com.nltv.chafenqi.view.info.chunithm

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.user.CFQUser

class InfoChunithmPageViewModel : ViewModel() {
    val user = CFQUser

    val chunithm = user.chunithm
    val info = chunithm.info
    val extra = chunithm.extra

    val currentCollection = info.last()
    val currentSkill = extra.skills.find { it.current }

    val characters = extra.characters
    val skills = extra.skills
    val nameplates = extra.nameplates
    val tickets = extra.tickets
    val mapIcons = extra.mapIcons
    val trophyGroups = extra.trophies.groupBy { it.type }
}