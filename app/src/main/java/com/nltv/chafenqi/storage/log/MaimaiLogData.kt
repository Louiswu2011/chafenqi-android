package com.nltv.chafenqi.storage.log

import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiDeltaEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

class MaimaiLogData(
    recentEntries: List<MaimaiRecentScoreEntry>,
    deltaEntries: List<MaimaiDeltaEntry>
) {
    data class MaimaiDayData(
        var date: Instant = Instant.fromEpochSeconds(0),
        var ratingGain: Int = 0,
        var playCountGain: Int = 0,
        var dxScoreGain: Int = 0,
        var totalAchievementGain: Double = 0.0,
        var syncPointGain: Int = 0,

        var latestDeltaEntry: MaimaiDeltaEntry = MaimaiDeltaEntry(),
        var recentEntries: List<MaimaiRecentScoreEntry> = listOf(),

        var hasDelta: Boolean = false
    )

    var dayPlayed = -1
    var records: List<MaimaiDayData> = listOf()

    init {
        val latestTimestamp = recentEntries.firstOrNull()?.timestamp ?: 0
        val oldestTimestamp = recentEntries.lastOrNull()?.timestamp ?: 0

        val truncatedOldestTimestamp = Instant
            .fromEpochSeconds(oldestTimestamp.toLong())
            .toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
            .date
            .atStartOfDayIn(timeZone = TimeZone.currentSystemDefault())
            .epochSeconds

        val truncatedLatestTimestamp = Instant
            .fromEpochSeconds(latestTimestamp.toLong())
            .toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
            .date
            .atStartOfDayIn(timeZone = TimeZone.currentSystemDefault())
            .plus(Duration.parse("24h"))
            .epochSeconds

        var pointer = truncatedOldestTimestamp
        while (pointer < truncatedLatestTimestamp) {
            pointer += 86400
            val playInDay = recentEntries.filter {
                it.timestamp in (pointer - 86400)..pointer
            }
            if (playInDay.isNotEmpty()) {
                val record = MaimaiDayData(
                    date = Instant.fromEpochSeconds(pointer - 86400),
                    recentEntries = playInDay
                )

                val latestDelta = deltaEntries.lastOrNull {
                    LocalDateTime.parse(it.createdAt, LocalDateTime.Formats.ISO)
                        .toInstant(TimeZone.currentSystemDefault())
                        .epochSeconds in (pointer - 86400)..pointer
                }
                if (latestDelta != null) record.latestDeltaEntry = latestDelta

                val previousDelta = records.lastOrNull()?.latestDeltaEntry
                if (previousDelta != null && latestDelta != null) {
                    record.hasDelta = true
                    record.ratingGain = latestDelta.rating - previousDelta.rating
                    record.playCountGain = latestDelta.playCount - previousDelta.playCount
                    record.totalAchievementGain = latestDelta.achievement - previousDelta.achievement
                    record.dxScoreGain = latestDelta.dxScore - previousDelta.dxScore
                    record.syncPointGain = latestDelta.syncPoint - previousDelta.syncPoint
                }

                records = records + record
            }
        }

        dayPlayed = records.size
    }
}