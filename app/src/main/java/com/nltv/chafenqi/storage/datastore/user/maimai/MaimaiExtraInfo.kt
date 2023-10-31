package com.nltv.chafenqi.storage.datastore.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiExtraInfo(
    val avatars: List<MaimaiAvatarEntry> = listOf(),
    val nameplates: List<MaimaiNameplateEntry> = listOf(),
    val characters: List<MaimaiCharacterEntry> = listOf(),
    val trophies: List<MaimaiTrophyEntry> = listOf(),
    val frames: List<MaimaiFrameEntry> = listOf(),
    val partners: List<MaimaiPartnerEntry> = listOf()
)
