package com.nltv.chafenqi.data

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class Comment(
    var id: Int = 0,
    var timestamp: Int = 0,
    @SerialName("userId") var uid: Int = 0,
    var username: String = "",
    var content: String = "",
    var gameType: Int = 0,
    var likes: Long = 0,
    var replyId: Int = 0
) {
    companion object {
        val dateFormat = LocalDateTime.Format {
            year()
            char('-')
            monthNumber()
            char('-')
            day()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }

    @OptIn(ExperimentalTime::class)
    val dateString: String
        get() = Instant.fromEpochSeconds(timestamp.toLong())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .format(dateFormat)
}
