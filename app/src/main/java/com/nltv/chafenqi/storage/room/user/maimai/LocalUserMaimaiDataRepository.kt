package com.nltv.chafenqi.storage.room.user.maimai

import kotlinx.coroutines.flow.Flow

class LocalUserMaimaiDataRepository(private val dao: UserMaimaiDataDao): UserMaimaiDataRepository {
    override val rawDao: UserMaimaiDataDao = dao

    override fun getAllBestScore(): Flow<List<MaimaiBestScoreEntry>> {
        return dao.getAllBestScores()
    }

    override fun getAllRecentScore(): Flow<List<MaimaiRecentScoreEntry>> {
        return dao.getAllRecentScores()
    }

    override fun getAllDeltas(): Flow<List<MaimaiDeltaEntry>> {
        return dao.getAllDeltas()
    }

    override fun getAllAvatars(): Flow<List<MaimaiAvatarEntry>> {
        return dao.getAllAvatars()
    }

    override fun getAllNameplates(): Flow<List<MaimaiNameplateEntry>> {
        return dao.getAllNameplates()
    }

    override fun getAllFrames(): Flow<List<MaimaiFrameEntry>> {
        return dao.getAllFrames()
    }

    override fun getAllTrophies(): Flow<List<MaimaiTrophyEntry>> {
        return dao.getAllTrophies()
    }

    override fun getAllCharacters(): Flow<List<MaimaiCharacterEntry>> {
        return dao.getAllCharacters()
    }

    override fun getAllPartners(): Flow<List<MaimaiPartnerEntry>> {
        return dao.getAllPartners()
    }
}