package com.nltv.chafenqi.storage.log

import com.nltv.chafenqi.model.user.maimai.UserMaimaiPlayerInfoEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
class MaimaiLogData(
    recentEntries: List<UserMaimaiRecentScoreEntry>,
    deltaEntries: List<UserMaimaiPlayerInfoEntry>
) {
    data class MaimaiDayData(
        var date: Instant = Instant.fromEpochSeconds(0),
        var ratingGain: Int = 0,
        var playCountGain: Int = 0,
        var dxScoreGain: Int = 0,
        var totalAchievementGain: Double = 0.0,
        var syncPointGain: Int = 0,

        var latestDeltaEntry: UserMaimaiPlayerInfoEntry = UserMaimaiPlayerInfoEntry(),
        var recentEntries: List<UserMaimaiRecentScoreEntry> = listOf(),

        var hasDelta: Boolean = false,

        var averageScore: Double = 0.0,
        var duration: Duration = Duration.ZERO,

        var durationString: String = ""
    )

    var dayPlayed = -1
    var records: List<MaimaiDayData> = listOf()

    init {
        val latestTimestamp = recentEntries.maxByOrNull { it.timestamp }?.timestamp ?: 0
        val oldestTimestamp = recentEntries.minByOrNull { it.timestamp }?.timestamp ?: 0

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

        var pointer = truncatedLatestTimestamp
        while (pointer > truncatedOldestTimestamp) {
            pointer -= 86400
            val playInDay = recentEntries.filter {
                it.timestamp in (pointer - 86400)..pointer
            }
            if (playInDay.isNotEmpty()) {
                val record = MaimaiDayData(
                    date = Instant.fromEpochSeconds(pointer - 86400),
                    recentEntries = playInDay
                )

                val latestDelta = deltaEntries.lastOrNull {
                    it.timestamp in (pointer - 86400)..pointer
                }
                if (latestDelta != null) record.latestDeltaEntry = latestDelta

                val previousDelta = records.lastOrNull()?.latestDeltaEntry
                if (previousDelta != null && latestDelta != null) {
                    record.hasDelta = true
                    record.ratingGain = latestDelta.rating - previousDelta.rating
                    record.playCountGain = latestDelta.playCount - previousDelta.playCount
                }

                if (record.recentEntries.isNotEmpty()) {
                    record.averageScore = record.recentEntries.sumOf { it.achievements.toDouble() } / record.recentEntries.size
                    val latest = record.recentEntries.maxByOrNull { it.timestamp } ?: continue
                    val earliest = record.recentEntries.minByOrNull { it.timestamp } ?: continue
                    record.duration = (latest.timestamp - earliest.timestamp).toDuration(DurationUnit.SECONDS)
                    record.durationString = record.duration.toComponents { hours, minutes, _, _ ->
                        if (hours > 0) {
                            "${hours}h ${minutes}m"
                        } else {
                            "${minutes}m"
                        }
                    }
                }

                records = records + record
            }
        }

        dayPlayed = records.size
    }

    fun reset() {
        records = listOf()
        dayPlayed = -1
    }
}