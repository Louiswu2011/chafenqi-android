package com.nltv.chafenqi.storage.datastore.user.maimai

import com.nltv.chafenqi.model.user.maimai.UserMaimaiAvatarEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiCharacterEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiFrameEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiNameplateEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiPartnerEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiTrophyEntry
import kotlinx.serialization.Serializable

@Serializable
data class MaimaiExtraInfo(
    val avatars: List<UserMaimaiAvatarEntry> = listOf(),
    val nameplates: List<UserMaimaiNameplateEntry> = listOf(),
    val characters: List<UserMaimaiCharacterEntry> = listOf(),
    val trophies: List<UserMaimaiTrophyEntry> = listOf(),
    val frames: List<UserMaimaiFrameEntry> = listOf(),
    val partners: List<UserMaimaiPartnerEntry> = listOf()
)
