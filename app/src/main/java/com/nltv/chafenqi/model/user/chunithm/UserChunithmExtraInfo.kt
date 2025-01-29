package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmExtraInfo(
    val nameplates: List<UserChunithmNameplateEntry> = listOf(),
    val skills: List<UserChunithmSkillEntry> = listOf(),
    val characters: List<UserChunithmCharacterEntry> = listOf(),
    val trophies: List<UserChunithmTrophyEntry> = listOf(),
    val mapIcons: List<UserChunithmMapIconEntry> = listOf(),
    val tickets: List<UserChunithmTicketEntry> = listOf()
)
