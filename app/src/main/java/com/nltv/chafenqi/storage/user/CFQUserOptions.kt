package com.nltv.chafenqi.storage.user

import android.util.Log
import com.nltv.chafenqi.networking.CFQServer

class CFQUserOptions() {
    companion object {
        const val tag = "CFQUserOptions"
    }

    private var authToken: String = ""
    var bindQQ: String = ""
    var fishToken: String = ""
    var forwardToFish: Boolean = false
    var forwardToLxns: Boolean = false
    var rateLimiting: Boolean = false
    var maimaiFavList: String = ""
    var chunithmFavList: String = ""

    suspend fun sync(authToken: String) {
        this.authToken = authToken

        fishToken = try {
            CFQServer.fishFetchToken(authToken)
        } catch (e: Exception) {
            Log.i(tag, "User did not bind fish account.")
            ""
        }.also { Log.i(tag, "Fetched user fish token: $fishToken") }

        forwardToFish = try {
            CFQServer.apiFetchUserOption(authToken, "forwarding_fish", "boolean").toBoolean()
        } catch (e: Exception) {
            Log.i(tag, "User fish forward option failed to load, fallback to false")
            false
        }.also { Log.i(tag, "Fetched user fish forward option: $forwardToFish") }

        forwardToFish = try {
            CFQServer.apiFetchUserOption(authToken, "forwarding_lxns", "boolean").toBoolean()
        } catch (e: Exception) {
            Log.i(tag, "User lxns forward option failed to load, fallback to false")
            false
        }.also { Log.i(tag, "Fetched user lxns forward option: $forwardToLxns") }

        rateLimiting = try {
            CFQServer.apiFetchUserOption(authToken, "rate_limiting", "boolean").toBoolean()
        } catch (e: Exception) {
            Log.i(tag, "User rate limiting option failed to load, fallback to false")
            false
        }.also { Log.i(tag, "Fetched user rate limiting option: $rateLimiting") }

        bindQQ = try {
            CFQServer.apiFetchUserCurrentQQ(authToken)
        } catch (e: Exception) {
            Log.i(tag, "User did not bind qq.")
            ""
        }.also { Log.i(tag, "Fetched user bind qq: $bindQQ") }

        maimaiFavList = fetchFavMusic(1)

        chunithmFavList = fetchFavMusic(0)
    }

    private suspend fun fetchFavMusic(gameType: Int): String {
        return try {
            CFQServer.apiFetchFavMusic(authToken, gameType) ?: throw IllegalStateException("Fav list is null.")
        } catch (e: Exception) {
            Log.i(tag, "User Fav list failed to fetch.")
            ""
        }.also { Log.i(tag, "Fetched user $gameType Fav list: $it") }
    }
}
