package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmRatingList(
    val best: List<UserChunithmRatingListEntry> = emptyList(),
    val new: List<UserChunithmRatingListEntry> = emptyList(),
    val candidate: List<UserChunithmRatingListEntry> = emptyList(),
) {
    val isEmpty = best.isEmpty() && new.isEmpty() && candidate.isEmpty()
}
