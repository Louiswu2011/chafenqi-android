package com.nltv.chafenqi.extension

import android.util.Log
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import java.security.MessageDigest
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
                    it.id == "299" && it.type == this.type
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
                    it.id == "299" && it.type == this.type
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