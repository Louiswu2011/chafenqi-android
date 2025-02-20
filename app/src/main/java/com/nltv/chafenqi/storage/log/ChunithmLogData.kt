package com.nltv.chafenqi.storage.log

import com.nltv.chafenqi.model.user.chunithm.UserChunithmPlayerInfoEntry
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ChunithmLogData(
    recentEntries: List<UserChunithmRecentScoreEntry>,
    deltaEntries: List<UserChunithmPlayerInfoEntry>
) {
    data class ChunithmDayData(
        var date: Instant = Instant.fromEpochSeconds(0),
        var ratingGain: Double = 0.0,
        var playCountGain: Int = 0,
        var overpowerGain: Double = 0.0,

        var latestDeltaEntry: UserChunithmPlayerInfoEntry = UserChunithmPlayerInfoEntry(),
        var recentEntries: List<UserChunithmRecentScoreEntry> = listOf(),

        var hasDelta: Boolean = false,
        var averageScore: Double = 0.0,
        var duration: Duration = Duration.ZERO,
        var durationString: String = ""
    )

    var dayPlayed = -1
    var records: List<ChunithmDayData> = listOf()

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

        var pointer = truncatedLatestTimestamp
        while (pointer > truncatedOldestTimestamp) {
            pointer -= 86400
            val playInDay = recentEntries.filter {
                it.timestamp in (pointer - 86400)..pointer
            }
            if (playInDay.isNotEmpty()) {
                val record = ChunithmDayData(
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
                    record.overpowerGain = latestDelta.rawOverpower - previousDelta.rawOverpower
                }

                if (record.recentEntries.isNotEmpty()) {
                    record.averageScore = record.recentEntries.sumOf { it.score } / record.recentEntries.size.toDouble()
                    val latest = record.recentEntries.maxByOrNull { it.timestamp } ?: continue
                    val earliest = record.recentEntries.minByOrNull { it.timestamp } ?: continue
                    record.duration = (latest.timestamp - earliest.timestamp).toDuration(
                        DurationUnit.SECONDS)
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