package com.nltv.chafenqi.extension

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.ui.Modifier
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
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

fun String.toMaimaiCoverPath(): String {
    return "https://www.diving-fish.com/covers/${this.toMaimaiCoverString()}.png"
}

fun String.toMaimaiTrophyType(): String {
    return when (this) {
        "NORMAL" -> "普通"
        "BRONZE" -> "铜"
        "SILVER" -> "银"
        "GOLD" -> "金"
        else -> "彩虹"
    }
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

fun Int.toDateString(context: Context): String {
    val hourFormat = if (DateFormat.is24HourFormat(context)) "HH" else "hh"
    val indicatorFormat = if (hourFormat == "hh") "a" else ""
    return Instant.ofEpochSecond(this.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd $hourFormat:mm $indicatorFormat"))
}

fun Int.toMonthDayString(): String {
    return Instant.ofEpochSecond(this.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("MM-dd"))
}

fun Int.toChunithmCoverPath(): String =
    "http://43.139.107.206:8083/api/chunithm/cover?musicId=${this}"

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

fun Float.toRateString(): String = when (this) {
    in 0.0..49.9999 -> "D"
    in 50.0000..59.0000 -> "C"
    in 60.0000..69.9999 -> "B"
    in 70.0000..74.9999 -> "BB"
    in 75.0000..79.9999 -> "BBB"
    in 80.0000..89.9999 -> "A"
    in 90.0000..93.0000 -> "AA"
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
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this).toDouble()
}

// TODO: Fix incorrect rating calculation
fun maimaiRatingOf(constant: Double, achievements: Float): Int {
    var factor = 0.0
    MAIMAI_RATING_FACTOR.forEach { entry ->
        if (entry.key.contains(achievements)) {
            factor = entry.value
        }
    }
    if (factor == 0.0) {
        factor = floor(achievements / 10.0)
    }

    return (constant * min(achievements, 100.5f) * factor / 100.0).toInt()
}

fun MaimaiBestScoreEntry.associatedMusicEntry(): MaimaiMusicEntry {
    return try {
        if (CFQPersistentData.Maimai.musicList.isNotEmpty()) {
            if (this.title.replace("　", " ") == "D✪N’T ST✪P R✪CKIN’") {
                CFQPersistentData.Maimai.musicList.first {
                    it.musicID == "364" && it.type == this.type
                }
            } else {
                CFQPersistentData.Maimai.musicList.first {
                    it.title.replace("　", " ") == this.title.replace("　", " ") &&
                            it.type.lowercase() == this.type.lowercase()
                }
            }
        } else {
            MaimaiMusicEntry()
        }
    } catch (e: Exception) {
        Log.e("我有意见", "找不到这首歌：${this.title}")
        MaimaiMusicEntry()
    }
}

fun MaimaiRecentScoreEntry.associatedMusicEntry(): MaimaiMusicEntry {
    return try {
        if (CFQUser.Maimai.best.isNotEmpty() && CFQPersistentData.Maimai.musicList.isNotEmpty()) {
            if (this.title.replace("　", " ") == "D✪N’T ST✪P R✪CKIN’") {
                CFQPersistentData.Maimai.musicList.first {
                    it.musicID == "364" && it.type == this.type
                }
            } else {
                CFQUser.Maimai.best.first {
                    it.title.replace("　", " ") == this.title.replace("　", " ") &&
                            it.type.lowercase() == this.type.lowercase()
                }.associatedMusicEntry
            }
        } else {
            MaimaiMusicEntry()
        }
    } catch (e: Exception) {
        Log.e("我有意见", "找不到这首歌：${this.title}")
        MaimaiMusicEntry()
    }
}

fun MaimaiBestScoreEntry.rating(): Int {
    return try {
        maimaiRatingOf(this.associatedMusicEntry.constants[this.levelIndex], this.achievements)
    } catch (e: Exception) {
        Log.e("MaimaiBestScoreEntry.Rating", "Error calculating rating for ${this.title}")
        -1
    }
}

fun ChunithmBestScoreEntry.associatedMusicEntry(): ChunithmMusicEntry {
    return try {
        if (CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
            CFQPersistentData.Chunithm.musicList.first {
                it.musicID.toString() == this.idx
            }
        } else {
            ChunithmMusicEntry()
        }
    } catch (e: Exception) {
        ChunithmMusicEntry()
    }
}

fun ChunithmRecentScoreEntry.associatedMusicEntry(): ChunithmMusicEntry {
    return try {
        if (CFQUser.Chunithm.best.isNotEmpty()) {
            CFQUser.Chunithm.best.first {
                it.idx == this.idx
            }.associatedMusicEntry
        } else {
            ChunithmMusicEntry()
        }
    } catch (e: Exception) {
        ChunithmMusicEntry()
    }
}

fun ChunithmRatingEntry.associatedMusicEntry(): ChunithmMusicEntry {
    return try {
        if (CFQUser.Chunithm.best.isNotEmpty()) {
            CFQUser.Chunithm.best.first {
                it.idx == this.idx
            }.associatedMusicEntry
        } else {
            ChunithmMusicEntry()
        }
    } catch (e: Exception) {
        ChunithmMusicEntry()
    }
}

fun chunithmRatingOf(constant: Double, score: Int): Double {
    return when (score) {
        in 925000..949999 ->
            constant - 3.0 + (score.toDouble() - 950000.0) * 3.0 / 50000.0

        in 950000..974999 ->
            constant - 1.5 + (score.toDouble() - 950000.0) * 3.0 / 50000.0

        in 975000..999999 ->
            constant + (score.toDouble() - 975000.0) / 2500.0 * 0.1

        in 1000000..1004999 ->
            constant + 1.0 + (score.toDouble() - 1000000.0) / 1000.0 * 0.1

        in 1005000..1007499 ->
            constant + 1.5 + (score.toDouble() - 1005000.0) / 500.0 * 0.1

        in 1007500..1008999 ->
            constant + 2.0 + (score.toDouble() - 1007500.0) / 100.0 * 0.01

        in 1009000..1010000 ->
            constant + 2.15

        else ->
            0.0
    }
}

fun ChunithmBestScoreEntry.rating(): Double =
    chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)

fun ChunithmRecentScoreEntry.rating(): Double =
    chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)

fun ChunithmRatingEntry.rating(): Double =
    chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}