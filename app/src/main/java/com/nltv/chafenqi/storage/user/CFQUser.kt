package com.nltv.chafenqi.storage.user

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import com.nltv.chafenqi.extension.CHUNITHM_LEVEL_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_LEVEL_STRINGS
import com.nltv.chafenqi.extension.RATE_STRINGS_CHUNITHM
import com.nltv.chafenqi.extension.RATE_STRINGS_MAIMAI
import com.nltv.chafenqi.extension.associatedMusicEntry
import com.nltv.chafenqi.extension.cutForRating
import com.nltv.chafenqi.extension.rating
import com.nltv.chafenqi.extension.toRateString
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
import com.nltv.chafenqi.storage.log.ChunithmLogData
import com.nltv.chafenqi.storage.log.MaimaiLogData
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.onesignal.OneSignal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class ChunithmRecentLineup(val entry: ChunithmRecentScoreEntry, val tag: String)
data class MaimaiRecentLineup(val entry: MaimaiRecentScoreEntry, val tag: String)

object CFQUser {
    private const val tag = "CFQUser"

    var remoteOptions = CFQUserOptions()

    var token = ""

    var username = ""
    var isPremium = false

    var mode = 1

    var maimai = Maimai
    var chunithm = Chunithm

    val isoTimeParser: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT
    val nameplateDateTimeFormatterWithIndicator: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MM-dd hh:mm a")
    val nameplateDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")

    object Maimai {
        var info = MaimaiUserInfo()
        var best = listOf<MaimaiBestScoreEntry>()
        var recent = listOf<MaimaiRecentScoreEntry>()
        var delta = listOf<MaimaiDeltaEntry>()
        var extra = MaimaiExtraInfo()
        var log: MaimaiLogData? = null

        var isBasicEmpty = true
        var isExtraEmpty = true

        val aux = Aux

        object Aux {
            var pastBest = listOf<MaimaiBestScoreEntry>()
            var newBest = listOf<MaimaiBestScoreEntry>()
            var pastRating: Int = 0
            var newRating: Int = 0
            var updateTime: String = ""

            val recommendList = mutableListOf<MaimaiRecentLineup>()
            val levelInfo = mutableListOf<MaimaiLevelInfo>()

            fun reset() {
                pastBest = listOf()
                newBest = listOf()
                pastRating = 0
                newRating = 0
                updateTime = ""
                recommendList.clear()
                levelInfo.clear()
            }
        }

        fun addAuxiliaryData(context: Context) {
            if (CFQPersistentData.Maimai.musicList.isNotEmpty()) {
                best.forEach {
                    it.associatedMusicEntry = it.associatedMusicEntry()
                }
                recent.forEach {
                    it.associatedMusicEntry = it.associatedMusicEntry()
                }
                best = best.filterNot { it.associatedMusicEntry == MaimaiMusicEntry() }
                recent = recent.filterNot { it.associatedMusicEntry == MaimaiMusicEntry() }

                val pastList = CFQPersistentData.Maimai.musicList.filter { !it.basicInfo.isNew }
                val (pastBest, newBest) = best.partition { bestEntry ->
                    pastList.map { it.title }.contains(bestEntry.title)
                }

                Aux.pastBest = pastBest.sortedByDescending { it.rating() }.take(35)
                Aux.newBest = newBest.sortedByDescending { it.rating() }.take(15)

                Aux.pastRating =
                    Aux.pastBest.fold(0) { acc, maimaiBestScoreEntry -> acc + maimaiBestScoreEntry.rating() }
                Aux.newRating =
                    Aux.newBest.fold(0) { acc, maimaiBestScoreEntry -> acc + maimaiBestScoreEntry.rating() }

                Aux.updateTime = Instant.from(isoTimeParser.parse(info.updatedAt))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(if (DateFormat.is24HourFormat(context)) nameplateDateFormatter else nameplateDateTimeFormatterWithIndicator)

                val mostRecent = recent.take(30).toMutableList()
                mostRecent.firstOrNull { it.fc == "applus" }
                    ?.also { Aux.recommendList.add(MaimaiRecentLineup(it, "AP+")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.firstOrNull { it.fc == "ap" }
                    ?.also { Aux.recommendList.add(MaimaiRecentLineup(it, "AP")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.firstOrNull { it.fc.startsWith("fc") || it.fc.startsWith("fs") }
                    ?.also { Aux.recommendList.add(MaimaiRecentLineup(it, "FC")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.maxByOrNull { it.achievements }
                    ?.also { Aux.recommendList.add(MaimaiRecentLineup(it, "高分")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.maxByOrNull { it.timestamp }
                    ?.also { Aux.recommendList.add(MaimaiRecentLineup(it, "最近一首")) }
                    ?.also { mostRecent.remove(it) }
                try {
                    mostRecent.filter { it.isNewRecord == 1 }
                        .maxByOrNull { it.timestamp }
                        ?.also { Aux.recommendList.add(MaimaiRecentLineup(it, "新纪录")) }
                } catch (_: Exception) {
                }

                MAIMAI_LEVEL_STRINGS.forEachIndexed { index, level ->
                    val levelMusicEntries =
                        CFQPersistentData.Maimai.musicList.filter { it.level.contains(level) }
                    val playedBestEntries = best.filter { it.level == level }
                    val playedMusicEntries = playedBestEntries.map { it.associatedMusicEntry }
                    val notPlayedMusicEntries =
                        levelMusicEntries.filterNot { playedMusicEntries.contains(it) }
                    val entryPerRate = mutableListOf<Int>()
                    RATE_STRINGS_MAIMAI.forEach { rate ->
                        if (rate == "其他") {
                            entryPerRate.add(playedBestEntries.filter { it.achievements < 94f }.size)
                        } else {
                            entryPerRate.add(playedBestEntries.filter { it.rateString == rate }.size)
                        }
                    }
                    entryPerRate.add(notPlayedMusicEntries.size)

                    Aux.levelInfo.add(
                        MaimaiLevelInfo(
                            levelString = level,
                            levelIndex = index,
                            playedBestEntries = playedBestEntries,
                            notPlayedMusicEntries = notPlayedMusicEntries,
                            musicCount = levelMusicEntries.size,
                            entryPerRate = entryPerRate
                        )
                    )
                }

                log = MaimaiLogData(recentEntries = recent, deltaEntries = delta)

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
            Aux.reset()
            log?.reset()
        }
    }

    object Chunithm {
        var info = ChunithmUserInfo()
        var best = listOf<ChunithmBestScoreEntry>()
        var recent = listOf<ChunithmRecentScoreEntry>()
        var delta = listOf<ChunithmDeltaEntry>()
        var rating = listOf<ChunithmRatingEntry>()
        var extra = ChunithmExtraEntry()
        var log: ChunithmLogData? = null

        var isBasicEmpty = true
        var isExtraEmpty = true

        var aux = Aux

        object Aux {
            var bestList = listOf<ChunithmRatingEntry>()
            var recentList = listOf<ChunithmRatingEntry>()
            var bestRating: Double = 0.0
            var recentRating: Double = 0.0
            var updateTime: String = ""

            val recommendList = mutableListOf<ChunithmRecentLineup>()
            val levelInfo = mutableListOf<ChunithmLevelInfo>()

            fun reset() {
                bestList = listOf()
                recentList = listOf()
                bestRating = 0.0
                recentRating = 0.0
                updateTime = ""
                recommendList.clear()
                levelInfo.clear()
            }
        }

        fun addAuxiliaryData(context: Context) {
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
                best.filterNot { it.associatedMusicEntry == ChunithmMusicEntry() }
                recent.filterNot { it.associatedMusicEntry == ChunithmMusicEntry() }
                rating.filterNot { it.associatedMusicEntry == ChunithmMusicEntry() }

                val (bestSlice, otherSlice) = rating.partition { it.type == "best" }
                val recentSlice = otherSlice.filter { it.type == "recent" }
                Aux.bestList = bestSlice
                Aux.recentList = recentSlice
                Aux.bestRating =
                    (bestSlice.fold(0.0) { acc, chunithmRatingEntry -> acc + chunithmRatingEntry.rating() } / 30).cutForRating()
                Aux.recentRating =
                    (recentSlice.fold(0.0) { acc, chunithmRatingEntry -> acc + chunithmRatingEntry.rating() } / 10).cutForRating()


                Aux.updateTime = Instant.from(isoTimeParser.parse(info.updatedAt))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(if (DateFormat.is24HourFormat(context)) nameplateDateFormatter else nameplateDateTimeFormatterWithIndicator)

                val mostRecent = recent.take(30).toMutableList()
                mostRecent.firstOrNull { it.score == 1010000 }
                    ?.also { Aux.recommendList.add(ChunithmRecentLineup(it, "理论值")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.firstOrNull { it.fullCombo == "alljustice" }
                    ?.also { Aux.recommendList.add(ChunithmRecentLineup(it, "AJ")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.firstOrNull {
                    it.fullCombo.contains("fullcombo") || it.fullChain.contains(
                        "fullchain"
                    )
                }
                    ?.also { Aux.recommendList.add(ChunithmRecentLineup(it, "FC")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.maxByOrNull { it.score }
                    ?.also { Aux.recommendList.add(ChunithmRecentLineup(it, "高分")) }
                    ?.also { mostRecent.remove(it) }
                mostRecent.maxByOrNull { it.timestamp }
                    ?.also { Aux.recommendList.add(ChunithmRecentLineup(it, "最近一首")) }
                    ?.also { mostRecent.remove(it) }
                try {
                    mostRecent.filter { it.isNewRecord == 1 }
                        .maxByOrNull { it.timestamp }
                        ?.also { Aux.recommendList.add(ChunithmRecentLineup(it, "新纪录")) }
                } catch (_: Exception) {
                }

                CHUNITHM_LEVEL_STRINGS.forEachIndexed { index, level ->
                    val levelMusicEntries = CFQPersistentData.Chunithm.musicList.filter {
                        it.charts.levels.contains(level)
                    }
                    val playedBestEntries =
                        best.filter { it.associatedMusicEntry.charts.levels[it.levelIndex] == level }
                    val playedMusicEntries = playedBestEntries.map { it.associatedMusicEntry }
                    val notPlayedMusicEntries =
                        levelMusicEntries.filterNot { playedMusicEntries.contains(it) }
                    val entryPerRate = mutableListOf<Int>()
                    RATE_STRINGS_CHUNITHM.forEach { rate ->
                        if (rate == "其他") {
                            entryPerRate.add(playedBestEntries.filter { it.score < 975000 }.size)
                        } else {
                            entryPerRate.add(playedBestEntries.filter { it.score.toRateString() == rate }.size)
                        }
                    }
                    entryPerRate.add(notPlayedMusicEntries.size)

                    Aux.levelInfo.add(
                        ChunithmLevelInfo(
                            levelString = level,
                            levelIndex = index,
                            playedBestEntries = playedBestEntries,
                            notPlayedMusicEntries = notPlayedMusicEntries,
                            musicCount = levelMusicEntries.size,
                            entryPerRate = entryPerRate
                        )
                    )
                }

                log = ChunithmLogData(recentEntries = recent, deltaEntries = delta)

                Log.i(tag, "Loaded chunithm auxiliary data.")
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
            Aux.reset()
            log?.reset()
        }
    }

    suspend fun createProfile(authToken: String, username: String) {
        token = authToken
        CFQUser.username = username

        isPremium = CFQServer.apiIsPremium(username)

        this.remoteOptions.sync(authToken)

        Log.i(tag, "User is${if (isPremium) "" else " not"} premium")
    }

    fun clearProfile() {
        token = ""
        username = ""
        isPremium = false

        remoteOptions = CFQUserOptions()
        Maimai.reset()
        Chunithm.reset()
    }

    suspend fun refreshPremiumStatus() {
        isPremium = CFQServer.apiIsPremium(username)
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