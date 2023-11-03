package com.nltv.chafenqi.extension

import android.util.Log
import com.nltv.chafenqi.storage.CFQUser
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
        return when(val number = this.toInt()) {
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

fun Int.toDateString(): String {
    return Instant.ofEpochSecond(this.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
}

fun Int.toChunithmCoverPath(): String = "http://43.139.107.206:8083/api/chunithm/cover?musicId=${this}"

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

    return (constant * min(achievements, 100.5f) * factor / 100).toInt()
}

fun MaimaiBestScoreEntry.associatedMusicEntry(): MaimaiMusicEntry {
    return try {
        if (CFQPersistentData.Maimai.musicList.isNotEmpty()) {
            if (this.title == "D✪N’T ST✪P R✪CKIN’") {
                CFQPersistentData.Maimai.musicList.first {
                    it.musicID == "299" && it.type == this.type
                }
            } else {
                CFQPersistentData.Maimai.musicList.first {
                    it.title.replace("　", " ") == this.title.replace("　", " ") &&
                            it.type == this.type
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
            if (this.title == "D✪N’T ST✪P R✪CKIN’") {
                CFQPersistentData.Maimai.musicList.first {
                    it.musicID == "299" && it.type == this.type
                }
            } else {
                CFQUser.Maimai.best.first {
                    it.title.replace("　", " ") == this.title.replace("　", " ") &&
                            it.type == this.type
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

fun MaimaiBestScoreEntry.rating(): Int = maimaiRatingOf(this.associatedMusicEntry.constants[this.levelIndex], this.achievements)

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
            constant - 3.0 + (score - 950000) * 3 / 50000
        in 950000..974999 ->
            constant - 1.5 + (score - 950000) * 3 / 50000
        in 975000..999999 ->
            constant + (score - 975000) / 2500 * 0.1
        in 1000000..1004999 ->
            constant + 1.0 + (score - 1000000) / 1000 * 0.1
        in 1005000..1007499 ->
            constant + 1.5 + (score - 1005000) / 500 * 0.1
        in 1007500..1008999 ->
            constant + 2.0 + (score - 1007500) / 100 * 0.01
        in 1009000..1010000 ->
            constant + 2.15
        else ->
            0.0
    }
}

fun ChunithmBestScoreEntry.rating(): Double = chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)
fun ChunithmRecentScoreEntry.rating(): Double = chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)
fun ChunithmRatingEntry.rating(): Double = chunithmRatingOf(this.associatedMusicEntry.charts.constants[this.levelIndex], this.score)
