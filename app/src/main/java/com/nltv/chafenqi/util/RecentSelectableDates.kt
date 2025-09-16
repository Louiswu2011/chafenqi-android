package com.nltv.chafenqi.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
class RecentSelectableDates (
    private val oldestMills: Long,
    private val latestMills: Long
): SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis in oldestMills..latestMills
    }

    @OptIn(ExperimentalTime::class)
    override fun isSelectableYear(year: Int): Boolean {
        val oldestYear = Instant
            .fromEpochMilliseconds(oldestMills)
            .toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
            .year
        val latestYear = Instant
            .fromEpochMilliseconds(latestMills)
            .toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
            .year
        return year in oldestYear..latestYear
    }
}