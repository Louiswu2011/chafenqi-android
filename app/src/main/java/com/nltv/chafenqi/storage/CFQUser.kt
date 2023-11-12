package com.nltv.chafenqi.storage

import android.util.Log
import com.nltv.chafenqi.extension.associatedMusicEntry
import com.nltv.chafenqi.extension.cutForRating
import com.nltv.chafenqi.extension.rating
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmDeltaEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmExtraEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmUserInfo
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiDeltaEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiExtraInfo
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiUserInfo
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.onesignal.OneSignal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CFQUser {
    private const val tag = "CFQUser"

    var token = ""
    var fishToken = ""

    var username = ""
    var isPremium = false

    var mode = 1

    var maimai = Maimai
    var chunithm = Chunithm

    val isoTimeParser = DateTimeFormatter.ISO_INSTANT
    val nameplateDateFormatter = DateTimeFormatter.ofPattern("MM-dd hh:mm")

    object Maimai {
        var info = MaimaiUserInfo()
        var best = listOf<MaimaiBestScoreEntry>()
        var recent = listOf<MaimaiRecentScoreEntry>()
        var delta = listOf<MaimaiDeltaEntry>()
        var extra = MaimaiExtraInfo()

        var isBasicEmpty = true
        var isExtraEmpty = true

        val aux = Aux

        object Aux {
            var pastBest = listOf<MaimaiBestScoreEntry>()
            var newBest = listOf<MaimaiBestScoreEntry>()
            var pastRating: Int = 0
            var newRating: Int = 0
            var updateTime: String = ""

            fun reset() {
                pastBest = listOf()
                newBest = listOf()
                pastRating = 0
                newRating = 0
                updateTime = ""
            }
        }

        fun addAuxiliaryData() {
            if (CFQPersistentData.Maimai.musicList.isNotEmpty()) {
                best.forEach {
                    it.associatedMusicEntry = it.associatedMusicEntry()
                }
                recent.forEach {
                    it.associatedMusicEntry = it.associatedMusicEntry()
                }

                val pastList = CFQPersistentData.Maimai.musicList.filter { !it.basicInfo.isNew }
                val (pastBest, newBest) = best.partition { bestEntry ->
                    pastList.map { it.title }.contains(bestEntry.title)
                }

                aux.pastBest = pastBest.sortedByDescending { it.rating() }.take(35)
                aux.newBest = newBest.sortedByDescending { it.rating() }.take(15)

                aux.pastRating =
                    aux.pastBest.fold(0) { acc, maimaiBestScoreEntry -> acc + maimaiBestScoreEntry.rating() }
                aux.newRating =
                    aux.newBest.fold(0) { acc, maimaiBestScoreEntry -> acc + maimaiBestScoreEntry.rating() }

                aux.updateTime = Instant.from(isoTimeParser.parse(info.updatedAt))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(nameplateDateFormatter)

                Log.i(tag, "Loaded maimai auxiliary data.")
            }
        }

        fun reset() {
            info = MaimaiUserInfo()
            best = listOf()
            recent = listOf()
            delta = listOf()
            extra = MaimaiExtraInfo()
            isBasicEmpty = true
            isExtraEmpty = true
            aux.reset()
        }
    }

    object Chunithm {
        var info = ChunithmUserInfo()
        var best = listOf<ChunithmBestScoreEntry>()
        var recent = listOf<ChunithmRecentScoreEntry>()
        var delta = listOf<ChunithmDeltaEntry>()
        var rating = listOf<ChunithmRatingEntry>()
        var extra = ChunithmExtraEntry()

        var isBasicEmpty = true
        var isExtraEmpty = true

        var aux = Aux

        object Aux {
            var bestList = listOf<ChunithmRatingEntry>()
            var recentList = listOf<ChunithmRatingEntry>()
            var bestRating: Double = 0.0
            var recentRating: Double = 0.0
            var updateTime: String = ""

            fun reset() {
                bestList = listOf()
                recentList = listOf()
                bestRating = 0.0
                recentRating = 0.0
                updateTime = ""
            }
        }

        fun addAuxiliaryData() {
            if (CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
                best.forEach {
                    it.associatedMusicEntry = it.associatedMusicEntry()
                }
                recent.forEach {
                    it.associatedMusicEntry = it.associatedMusicEntry()
                }
                rating.forEach {
                    it.associatedMusicEntry = it.associatedMusicEntry()
                }

                val (bestSlice, otherSlice) = rating.partition { it.type == "best" }
                val recentSlice = otherSlice.filter { it.type == "recent" }
                aux.bestList = bestSlice
                aux.recentList = recentSlice
                aux.bestRating =
                    (bestSlice.fold(0.0) { acc, chunithmRatingEntry -> acc + chunithmRatingEntry.rating() } / 30).cutForRating()
                aux.recentRating =
                    (recentSlice.fold(0.0) { acc, chunithmRatingEntry -> acc + chunithmRatingEntry.rating() } / 10).cutForRating()

                aux.updateTime = Instant.from(isoTimeParser.parse(info.updatedAt))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(nameplateDateFormatter)
            }
        }

        fun reset() {
            info = ChunithmUserInfo()
            best = listOf()
            recent = listOf()
            delta = listOf()
            rating = listOf()
            extra = ChunithmExtraEntry()
            isBasicEmpty = true
            isExtraEmpty = true
            aux.reset()
        }
    }

    suspend fun createProfile(authToken: String, username: String) {
        this.token = authToken
        this.username = username

        this.isPremium = CFQServer.apiIsPremium(username)
        this.fishToken = try {
            CFQServer.fishFetchToken(authToken)
        } catch (e: Exception) {
            ""
        }

        Log.i(tag, "User is ${if (isPremium) "" else "not"} premium")
        // registerOneSignal(username)
    }

    suspend fun loadProfileFromCache(targetUsername: String) {}

    fun clearProfile() {
        token = ""
        username = ""
        isPremium = false

        maimai.reset()
        chunithm.reset()
    }

    suspend fun refreshPremiumStatus() {
        this.isPremium = CFQServer.apiIsPremium(username)
    }

    fun registerOneSignal(username: String) {
        OneSignal.login(username)
        Log.i(tag, "Registered OneSignal as external ID \"$username\".")
    }

    fun logoutOneSignal() {
        OneSignal.logout()
        Log.i(tag, "Logged out from OneSignal.")
    }
}