package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmExtraEntry(
    val nameplates: List<ChunithmNameplateEntry> = listOf(),
    val skills: List<ChunithmSkillEntry> = listOf(),
    val characters: List<ChunithmCharacterEntry> = listOf(),
    val trophies: List<ChunithmTrophyEntry> = listOf(),
    val mapIcons: List<ChunithmMapIconEntry> = listOf(),
    val tickets: List<ChunithmTicketEntry> = listOf(),
    val collections: List<ChunithmCollectionEntry> = listOf()
)
