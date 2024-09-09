package com.nltv.chafenqi.data

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    var id: Int = 0,
    var timestamp: Int = 0,
    var uid: Int = 0,
    var username: String = "",
    var content: String = "",
    var musicId: Int = 0,
    @SerialName("musicFrom") var gameType: Int = 0,
    @SerialName("reply") var replyId: Int = 0
) {
    companion object {
        val dateFormat = LocalDateTime.Format {
            year()
            char('-')
            monthNumber()
            char('-')
            dayOfMonth()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }

    val dateString: String
        get() = Instant.fromEpochSeconds(timestamp.toLong())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .format(dateFormat)
}
