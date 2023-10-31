package com.nltv.chafenqi.storage.datastore.user.maimai

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json
import kotlinx.serialization.Serializable

@Serializable
data class MaimaiNameplateEntry(
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val area: String = "",
    val selected: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)
