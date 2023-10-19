package com.nltv.chafenqi.storage.room.user.maimai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMaimaiDataDao {
    @Upsert
    suspend fun upsertBestScore(entry: MaimaiBestScoreEntry)
    @Query("SELECT * from maimaiBestScore")
    fun getAllBestScores(): Flow<List<MaimaiBestScoreEntry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecentScore(entry: MaimaiRecentScoreEntry)
    @Query("SELECT * from maimaiRecentScore")
    fun getAllRecentScores(): Flow<List<MaimaiRecentScoreEntry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeltaEntry(entry: MaimaiDeltaEntry)
    @Query("SELECT * from maimaiDeltaEntries")
    fun getAllDeltas(): Flow<List<MaimaiDeltaEntry>>

    @Upsert
    suspend fun upsertAvatar(entry: MaimaiAvatarEntry)
    @Query("SELECT * from maimaiAvatarEntries")
    fun getAllAvatars(): Flow<List<MaimaiAvatarEntry>>
    @Upsert
    suspend fun upsertNameplate(entry: MaimaiNameplateEntry)
    @Query("SELECT * from maimaiNameplateEntries")
    fun getAllNameplates(): Flow<List<MaimaiNameplateEntry>>
    @Upsert
    suspend fun upsertFrame(entry: MaimaiFrameEntry)
    @Query("SELECT * from maimaiFrameEntries")
    fun getAllFrames(): Flow<List<MaimaiFrameEntry>>
    @Upsert
    suspend fun upsertTrophy(entry: MaimaiTrophyEntry)
    @Query("SELECT * from maimaiTrophyEntries")
    fun getAllTrophies(): Flow<List<MaimaiTrophyEntry>>
    @Upsert
    suspend fun upsertCharacter(entry: MaimaiCharacterEntry)
    @Query("SELECT * from maimaiCharacterEntries")
    fun getAllCharacters(): Flow<List<MaimaiCharacterEntry>>
    @Upsert
    suspend fun upsertPartner(entry: MaimaiPartnerEntry)
    @Query("SELECT * from maimaiPartnerEntries")
    fun getAllPartners(): Flow<List<MaimaiPartnerEntry>>
}