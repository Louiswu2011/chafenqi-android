package com.nltv.chafenqi.extension

import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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