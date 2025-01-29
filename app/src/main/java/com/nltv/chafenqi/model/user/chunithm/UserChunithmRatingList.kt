package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmRatingList(
    val best: List<UserChunithmRatingListEntry> = emptyList(),
    val recent: List<UserChunithmRatingListEntry> = emptyList(),
    val candidate: List<UserChunithmRatingListEntry> = emptyList(),
) {
    val isEmpty = best.isEmpty() && recent.isEmpty() && candidate.isEmpty()
}
