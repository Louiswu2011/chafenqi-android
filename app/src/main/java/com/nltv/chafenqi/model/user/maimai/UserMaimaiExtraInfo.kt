package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiExtraInfo(
    val avatars: List<UserMaimaiAvatarEntry> = listOf(),
    val nameplates: List<UserMaimaiNameplateEntry> = listOf(),
    val characters: List<UserMaimaiCharacterEntry> = listOf(),
    val trophies: List<UserMaimaiTrophyEntry> = listOf(),
    val frames: List<UserMaimaiFrameEntry> = listOf(),
    val partners: List<UserMaimaiPartnerEntry> = listOf()
)
