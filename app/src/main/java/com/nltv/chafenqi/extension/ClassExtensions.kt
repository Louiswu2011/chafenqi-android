package com.nltv.chafenqi.extension

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.ui.Modifier
import com.nltv.chafenqi.model.user.chunithm.UserChunithmBestScoreEntry
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRatingListEntry
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiBestScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.floor
import kotlin.math.min

val MAIMAI_RATING_FACTOR = mapOf(
    100.5000..101.0000 to 22.4,
    100.0000..100.4999 to 21.6,
    99.5000..99.9999 to 21.1,
    99.0000..99.4999 to 20.8,
    98.0000..98.9999 to 20.3,
    97.0000..97.9999 to 20.0,
    94.0000..96.9999 to 16.8
)

fun String.sha256(): String {
    return MessageDigest
        .getInstance("SHA-256")
        .digest(this.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}

fun String.toMaimaiCoverString(): String {
    try {
        return when (val number = this.toInt()) {
            in 10000..11000 -> {
                val rawId = number - 10000
                rawId.toString().prepended()
            }

            in 0..10000 -> {
                this.prepended()
            }

            else -> this
        }
    } catch (e: NumberFormatException) {
        return "1"
    }
}

fun String.prepended(): String {
    var prepended = this
    while (prepended.length < 5) {
        prepended = "0$prepended"
    }
    return prepended
}

fun Int.toMaimaiCoverPath(): String = "https://assets2.lxns.net/maimai/jacket/$this.png"

fun String.toMaimaiTrophyType(): String {
    return when (this) {
        "NORMAL" -> "普通"
        "BRONZE" -> "铜"
        "SILVER" -> "银"
        "GOLD" -> "金"
        else -> "彩虹"
    }
}

fun String.toLevelIndex(mode: Int): Int {
    return if (mode == 0) CHUNITHM_LEVEL_STRINGS.indexOf(this) else MAIMAI_LEVEL_STRINGS.indexOf(
        this
    )
}

fun String.toChunithmTrophyType(): String {
    return when (this) {
        "normal" -> "普通"
        "copper" -> "铜"
        "silver" -> "银"
        "gold" -> "金"
        "platinum" -> "白金"
        else -> "彩虹"
    }
}

fun Int.toDateString(context: Context) = this.toLong().toDateString(context)

fun Long.toDateString(context: Context): String {
    val hourFormat = if (DateFormat.is24HourFormat(context)) "HH" else "hh"
    val indicatorFormat = if (hourFormat == "hh") "a" else ""
    return Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd $hourFormat:mm $indicatorFormat"))
}

fun Int.toMonthDayString() = this.toLong().toMonthDayString()

fun Long.toMonthDayString(): String {
    return Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("MM-dd"))
}

fun Long.toYMDString(): String {
    return Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun Int.toChunithmCoverPath(): String =
    "${CFQServer.defaultPath}/api/chunithm/cover?musicId=${this}"

fun Int.toMaimaiLevelString(): String {
    if (this <= 6) return this.toString()
    if (this % 2 != 0) return ((this - 7) / 2 + 7).toString()
    return ((this - 8) / 2 + 7).toString() + "+"
}

fun Int.toRateString(): String = when (this) {
    in Int.MIN_VALUE..499999 -> "D"
    in 500000..599999 -> "C"
    in 600000..699999 -> "B"
    in 700000..799999 -> "BB"
    in 800000..899999 -> "BBB"
    in 900000..924999 -> "A"
    in 925000..949999 -> "AA"
    in 950000..974999 -> "AAA"
    in 975000..989999 -> "S"
    in 990000..999999 -> "S+"
    in 1000000..1004999 -> "SS"
    in 1005000..1007499 -> "SS+"
    in 1007500..1008999 -> "SSS"
    in 1009000..1010000 -> "SSS+"
    else -> "D"
}

fun Double.toRateString(): String = when (this) {
    in 0.0..49.9999 -> "D"
    in 50.0000..59.0000 -> "C"
    in 60.0000..69.9999 -> "B"
    in 70.0000..74.9999 -> "BB"
    in 75.0000..79.9999 -> "BBB"
    in 80.0000..89.9999 -> "A"
    in 90.0000..93.9999 -> "AA"
    in 94.0000..96.9999 -> "AAA"
    in 97.0000..97.9999 -> "S"
    in 98.0000..98.9999 -> "S+"
    in 99.0000..99.4999 -> "SS"
    in 99.5000..99.9999 -> "SS+"
    in 100.0000..100.4999 -> "SSS"
    in 100.5000..101.0000 -> "SSS+"
    else -> ""
}

fun Double.cutForRating(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    return df.format(this).toDouble()
}

// TODO: Fix incorrect rating calculation
fun maimaiRatingOf(constant: Double, achievements: Double): Int {
    var factor = 0.0
    MAIMAI_RATING_FACTOR.forEach { entry ->
        if (entry.key.contains(achievements)) {
            factor = entry.value
        }
    }
    if (factor == 0.0) {
        factor = floor(achievements / 10.0)
    }

    return (constant * min(achievements, 100.5) * factor / 100.0).toInt()
//    val achievementsCapped = achievements.toBigDecimal().min("100.5".toBigDecimal())
//    return constant
//        .toBigDecimal()
//        .times(achievementsCapped)
//        .times(factor)
//        .divide(BigDecimal(100))
//        .toBigInteger()
//        .toInt()
}

fun UserMaimaiBestScoreEntry.associatedMusicEntry(): MaimaiMusicEntry {
    return try {
        val musicList = CFQPersistentData.Maimai.musicList
        if (musicList.isNotEmpty()) {
            musicList.first { it.musicId == musicId }
        } else {
            Log.e("UserMaimaiBestScoreEntry", "无法匹配歌曲：${this.musicId}")
            MaimaiMusicEntry()
        }
    } catch (e: Exception) {
        Log.e("UserMaimaiBestScoreEntry", "找不到这首歌：${this.musicId}, $e")
        MaimaiMusicEntry()
    }
}

fun UserMaimaiRecentScoreEntry.associatedMusicEntry(): MaimaiMusicEntry {
    return try {
        val musicList = CFQPersistentData.Maimai.musicList
        if (CFQUser.Maimai.best.isNotEmpty() && musicList.isNotEmpty()) {
            CFQUser.Maimai.best.firstOrNull {
                it.musicId == musicId
            }?.associatedMusicEntry ?: musicList.first { it.musicId == musicId }
        } else {
            Log.e("UserMaimaiRecentScoreEntry", "无法匹配歌曲：${this.musicId}")
            MaimaiMusicEntry()
        }
    } catch (e: Exception) {
        Log.e("UserMaimaiRecentScoreEntry", "找不到这首歌：${this.musicId}, $e")
        MaimaiMusicEntry()
    }
}

fun UserMaimaiBestScoreEntry.rating(): Int {
    return try {
        maimaiRatingOf(this.associatedMusicEntry.constants[this.levelIndex], this.achievements)
    } catch (e: Exception) {
        Log.e("MaimaiBestScoreEntry.Rating", "Error calculating rating for ${this.musicId}")
        -1
    }
}

fun UserChunithmBestScoreEntry.associatedMusicEntry(): ChunithmMusicEntry {
    return try {
        if (CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
            CFQPersistentData.Chunithm.musicList.first {
                // it.musicID.toString() == this.idx
                it.musicId == musicId
            }
        } else {
            Log.e("我有意见", "无法匹配歌曲：${this.musicId}")
            ChunithmMusicEntry()
        }
    } catch (e: Exception) {
        Log.e("我有意见", "找不到这首歌：${this.musicId}")
        ChunithmMusicEntry()
    }
}

fun UserChunithmRecentScoreEntry.associatedMusicEntry(): ChunithmMusicEntry {
    return try {
        if (CFQUser.Chunithm.best.isNotEmpty()) {
            CFQUser.Chunithm.best.first {
                it.musicId == musicId
            }.associatedMusicEntry
        } else {
            Log.e("我有意见", "无法匹配歌曲：${this.musicId}")
            ChunithmMusicEntry()
        }
    } catch (e: Exception) {
        ChunithmMusicEntry()
    }
}

fun UserChunithmRatingListEntry.associatedMusicEntry(): ChunithmMusicEntry {
    return try {
        if (CFQUser.Chunithm.best.isNotEmpty()) {
            CFQUser.Chunithm.best.first {
                it.musicId == musicId
            }.associatedMusicEntry
        } else {
            Log.e("我有意见", "无法匹配歌曲：${this.musicId}")
            ChunithmMusicEntry()
        }
    } catch (e: Exception) {
        ChunithmMusicEntry()
    }
}

fun chunithmRatingOf(constant: Double, score: Int): Double {
    return when (score) {
        in 800000..899999 ->
            (constant - 5.0) / 2

        in 900000..924999 ->
            constant - 5.0

        in 925000..974999 ->
            constant - 3.0

        in 975000..989999 ->
            constant + (score.toDouble() - 975000.0) / 2500.0 * 0.1

        in 990000..999999 ->
            constant + 0.6 + (score.toDouble() - 990000) / 2500 * 0.1

        in 1000000..1004999 ->
            constant + 1.0 + (score.toDouble() - 1000000.0) / 1000.0 * 0.1

        in 1005000..1007499 ->
            constant + 1.5 + (score.toDouble() - 1005000.0) / 50.0 * 0.01

        in 1007500..1008999 ->
            constant + 2.0 + (score.toDouble() - 1007500.0) / 100.0 * 0.01

        in 1009000..1010000 ->
            constant + 2.15

        else ->
            0.0
    }
}

fun UserChunithmBestScoreEntry.rating(): Double =
    chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)

fun UserChunithmRecentScoreEntry.rating(): Double =
    chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)

fun UserChunithmRatingListEntry.rating(): Double =
    chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

fun Int.toGameTypeString() = when (this) {
    0 -> "chunithm"
    1 -> "maimai"
    else -> throw IllegalArgumentException("Invalid game type: $this")
}

fun String.toHalfWidth(): String {
    val builder = StringBuilder()
    for (char in this) {
        val code = char.code
        when (code) {
            in 65281..65374 -> {
                // Full-width ASCII variants
                builder.append((code - 65248).toChar())
            }
            12288 -> {
                // Full-width space
                builder.append(' ')
            }
            else -> {
                builder.append(char)
            }
        }
    }
    return builder.toString()
}