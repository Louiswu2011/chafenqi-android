package com.nltv.chafenqi

import android.content.Context
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.Dispatchers
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
                val infoString = CFQServer.apiMaimai("info", token)
                val bestString = CFQServer.apiMaimai("best", token)
                val recentString = CFQServer.apiMaimai("recent", token)

                maimai.info = deserializer.decodeFromString(infoString)
                maimai.best = deserializer.decodeFromString(bestString)
                maimai.recent = deserializer.decodeFromString(recentString)

                maimai.isBasicEmpty = false
                Log.i(tag, "Loaded user maimai basic data.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(tag, "User maimai data is empty, skipping...")
                isEmpty = true
            }

            if (user.isPremium && !isEmpty) {
                try {
                    val deltaString = CFQServer.apiMaimai("delta", token)
                    val extraString = CFQServer.apiMaimai("extra", token)

                    maimai.delta = deserializer.decodeFromString(deltaString)
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
                val infoString = CFQServer.apiChunithm("info", token)
                val bestString = CFQServer.apiChunithm("best", token)
                val recentString = CFQServer.apiChunithm("recent", token)
                val ratingString = CFQServer.apiChunithm("rating", token)

                chunithm.info = deserializer.decodeFromString(infoString)
                chunithm.best = deserializer.decodeFromString(bestString)
                chunithm.recent = deserializer.decodeFromString(recentString)
                chunithm.rating = deserializer.decodeFromString(ratingString)

                chunithm.isBasicEmpty = false
                Log.i(tag, "Loaded user chunithm basic data.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(tag, "User chunithm data is empty, skipping...")
                isEmpty = true
            }

            if (user.isPremium && !isEmpty) {
                try {
                    val deltaString = CFQServer.apiChunithm("delta", token)
                    val extraString = CFQServer.apiChunithm("extras", token)

                    chunithm.delta = deserializer.decodeFromString(deltaString)
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

                chunithm.best.filterNot { it.associatedMusicEntry.isWE }
                chunithm.recent.filterNot { it.associatedMusicEntry.isWE }
                chunithm.rating.filterNot { it.associatedMusicEntry.isWE }
            }
        }
    }
}

val LocalUserState = compositionLocalOf<CFQUserStateViewModel> { error("User state not found.") }