package com.nltv.chafenqi.storage.log

import androidx.compose.ui.util.fastFirst
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiDeltaEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.parse
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
                    DateTimeComponents.parse(it.createdAt.replaceFirst(' ', 'T').filterNot { char -> char.isWhitespace() }, DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET)
                        .toLocalDateTime()
                        .toInstant(TimeZone.currentSystemDefault())
                        .epochSeconds in (pointer - 86400)..pointer
                }
                if (latestDelta != null) record.latestDeltaEntry = latestDelta

                val previousDelta = records.lastOrNull()?.latestDeltaEntry
                if (previousDelta != null && latestDelta != null) {
                    record.hasDelta = true
                    record.ratingGain = latestDelta.rating - previousDelta.rating
                    record.playCountGain = latestDelta.playCount - previousDelta.playCount
                    record.totalAchievementGain =
                        latestDelta.achievement - previousDelta.achievement
                    record.dxScoreGain = latestDelta.dxScore - previousDelta.dxScore
                    record.syncPointGain = latestDelta.syncPoint - previousDelta.syncPoint
                }

                if (record.recentEntries.isNotEmpty()) {
                    record.averageScore = record.recentEntries.sumOf { it.achievements.toDouble() } / record.recentEntries.size
                    record.duration = (record.recentEntries.maxBy { it.timestamp }.timestamp - record.recentEntries.minBy { it.timestamp }.timestamp).toDuration(DurationUnit.SECONDS)
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