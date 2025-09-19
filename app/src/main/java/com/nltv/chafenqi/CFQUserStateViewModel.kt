package com.nltv.chafenqi

import android.content.Context
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.Firebase
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CFQUserStateViewModel : ViewModel() {
    val user = CFQUser
    var isLoggedIn by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)

    fun logout() {
        user.clearProfile()
        Firebase.crashlytics.setUserId("")
        isLoggedIn = false
    }

    suspend fun loadMaimaiData(context: Context) {
        val tag = "Login.User.MaimaiData"
        val token = user.token
        val deserializer = Json { ignoreUnknownKeys = true }

        val maimai = CFQUser.Maimai

        var isEmpty = false

        withContext(Dispatchers.IO) {
            try {
                val infoString = async { CFQServer.apiMaimai("info", token) }
                val bestString = async { CFQServer.apiMaimai("best", token) }
                val recentString = async { CFQServer.apiMaimai("recent", token) }

                maimai.info = deserializer.decodeFromString(infoString.await())
                maimai.best = deserializer.decodeFromString(bestString.await())
                maimai.recent = deserializer
                    .decodeFromString<List<UserMaimaiRecentScoreEntry>>(recentString.await())
                    .sortedByDescending { it.timestamp }

                maimai.isBasicEmpty = false
                Log.i(tag, "Loaded user maimai basic data.")
            } catch (e: Exception) {
                e.printStackTrace()
                Firebase.crashlytics.apply {
                    setCustomKey("error_location", "maimai_data_loading")
                    setCustomKey("error_type", e::class.simpleName ?: "Unknown")
                    recordException(e)
                }
                Log.e(tag, "User maimai data is empty, skipping...")
                isEmpty = true
            }

            if (user.isPremium && !isEmpty) {
                try {
                    val extraString = CFQServer.apiMaimai("extra", token)

                    maimai.extra = deserializer.decodeFromString(extraString)
                    maimai.isExtraEmpty = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(tag, "Error loading user maimai premium data.")
                }
            }

            if (!maimai.isBasicEmpty) {
                maimai.addAuxiliaryData(context)
            }
        }
    }

    suspend fun loadChunithmData(context: Context) {
        val tag = "Login.User.ChunithmData"
        val token = user.token
        val deserializer = Json { ignoreUnknownKeys = true }

        val chunithm = CFQUser.Chunithm

        var isEmpty = false

        withContext(Dispatchers.IO) {
            try {
                val infoString = async { CFQServer.apiChunithm("info", token) }
                val bestString = async { CFQServer.apiChunithm("best", token) }
                val recentString = async { CFQServer.apiChunithm("recent", token) }
                val ratingString = async { CFQServer.apiChunithm("rating", token) }

                chunithm.info = deserializer.decodeFromString(infoString.await())
                chunithm.best = deserializer.decodeFromString(bestString.await())
                chunithm.recent = deserializer
                    .decodeFromString<List<UserChunithmRecentScoreEntry>>(recentString.await())
                    .sortedByDescending { it.timestamp }
                chunithm.rating = deserializer.decodeFromString(ratingString.await())

                chunithm.isBasicEmpty = false
                Log.i(tag, "Loaded user chunithm basic data.")
            } catch (e: Exception) {
                e.printStackTrace()
                Firebase.crashlytics.apply {
                    setCustomKey("error_location", "chunithm_data_loading")
                    setCustomKey("error_type", e::class.simpleName ?: "Unknown")
                    recordException(e)
                }
                Log.e(tag, "User chunithm data is empty, skipping...")
                isEmpty = true
            }

            if (user.isPremium && !isEmpty) {
                try {
                    val extraString = CFQServer.apiChunithm("extra", token)

                    chunithm.extra = deserializer.decodeFromString(extraString)
                    chunithm.isExtraEmpty = false
                    Log.i(tag, "Loaded user chunithm premium data.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(tag, "Error loading user chunithm premium data.")
                }
            }

            if (!chunithm.isBasicEmpty) {
                chunithm.addAuxiliaryData(context)

                chunithm.best = chunithm.best.filterNot { it.associatedMusicEntry.isWE }
                chunithm.recent = chunithm.recent.filterNot { it.associatedMusicEntry.isWE }

                val bestFiltered = chunithm.best.filterNot { it.associatedMusicEntry.isWE }
                val newFiltered = chunithm.recent.filterNot { it.associatedMusicEntry.isWE }
                val candidateFiltered = chunithm.rating.candidate.filterNot { it.associatedMusicEntry.isWE }

                if (bestFiltered.size != chunithm.rating.best.size || 
                    newFiltered.size != chunithm.rating.new.size ||
                    candidateFiltered.size != chunithm.rating.candidate.size) {
                    chunithm.rating = chunithm.rating.copy(
                        best = chunithm.rating.best.filterNot { it.associatedMusicEntry.isWE },
                        new = chunithm.rating.new.filterNot { it.associatedMusicEntry.isWE },
                        candidate = chunithm.rating.candidate.filterNot { it.associatedMusicEntry.isWE }
                    )
                }
            }
        }
    }
}

val LocalUserState = compositionLocalOf<CFQUserStateViewModel> { error("User state not found.") }