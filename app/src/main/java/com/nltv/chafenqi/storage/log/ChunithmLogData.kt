package com.nltv.chafenqi.storage.log

import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmDeltaEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.parse
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ChunithmLogData(
    recentEntries: List<ChunithmRecentScoreEntry>,
    deltaEntries: List<ChunithmDeltaEntry>
) {
    data class ChunithmDayData(
        var date: Instant = Instant.fromEpochSeconds(0),
        var ratingGain: Double = 0.0,
        var playCountGain: Int = 0,
        var overpowerGain: Double = 0.0,

        var latestDeltaEntry: ChunithmDeltaEntry = ChunithmDeltaEntry(),
        var recentEntries: List<ChunithmRecentScoreEntry> = listOf(),

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
                    record.overpowerGain = latestDelta.rawOverpower - previousDelta.rawOverpower
                }

                if (record.recentEntries.isNotEmpty()) {
                    record.averageScore = record.recentEntries.sumOf { it.score } / record.recentEntries.size.toDouble()
                    record.duration = (record.recentEntries.maxBy { it.timestamp }.timestamp - record.recentEntries.minBy { it.timestamp }.timestamp).toDuration(
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