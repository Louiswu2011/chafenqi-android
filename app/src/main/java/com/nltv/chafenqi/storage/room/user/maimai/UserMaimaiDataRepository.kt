package com.nltv.chafenqi.storage.room.user.maimai

import kotlinx.coroutines.flow.Flow

interface UserMaimaiDataRepository {
    val rawDao: UserMaimaiDataDao
    fun getAllBestScore(): Flow<List<MaimaiBestScoreEntry>>
    fun getAllRecentScore(): Flow<List<MaimaiRecentScoreEntry>>
    fun getAllDeltas(): Flow<List<MaimaiDeltaEntry>>
    fun getAllAvatars(): Flow<List<MaimaiAvatarEntry>>
    fun getAllNameplates(): Flow<List<MaimaiNameplateEntry>>
    fun getAllFrames(): Flow<List<MaimaiFrameEntry>>
    fun getAllTrophies(): Flow<List<MaimaiTrophyEntry>>
    fun getAllCharacters(): Flow<List<MaimaiCharacterEntry>>
    fun getAllPartners(): Flow<List<MaimaiPartnerEntry>>
}