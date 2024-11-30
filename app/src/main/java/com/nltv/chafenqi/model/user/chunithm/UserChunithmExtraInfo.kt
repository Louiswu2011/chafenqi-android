package com.nltv.chafenqi.model.user.chunithm

data class UserChunithmExtraInfo(
    val nameplates: List<UserChunithmNameplateEntry> = listOf(),
    val skills: List<UserChunithmSkillEntry> = listOf(),
    val characters: List<UserChunithmCharacterEntry> = listOf(),
    val trophies: List<UserChunithmTrophyEntry> = listOf(),
    val mapIcons: List<UserChunithmMapIconEntry> = listOf(),
    val tickets: List<UserChunithmTicketEntry> = listOf()
)
