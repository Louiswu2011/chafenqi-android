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
            CFQServer.apiFetchUserOption(authToken, "forwarding_fish") == "1"
        } catch (e: Exception) {
            Log.i(tag, "User fish forward option failed to load, fallback to false")
            false
        }.also { Log.i(tag, "Fetched user fish forward option: $forwardToFish") }

        forwardToFish = try {
            CFQServer.apiFetchUserOption(authToken, "forwarding_lxns") == "1"
        } catch (e: Exception) {
            Log.i(tag, "User lxns forward option failed to load, fallback to false")
            false
        }.also { Log.i(tag, "Fetched user lxns forward option: $forwardToLxns") }

        rateLimiting = try {
            CFQServer.apiFetchUserOption(authToken, "rate_limiting") == "1"
        } catch (e: Exception) {
            Log.i(tag, "User rate limiting option failed to load, fallback to false")
            false
        }.also { Log.i(tag, "Fetched user rate limiting option: $rateLimiting") }

        bindQQ = try {
            CFQServer.apiFetchUserOption(authToken, "bindQQ")
        } catch (e: Exception) {
            Log.i(tag, "User did not bind qq.")
            ""
        }.also { Log.i(tag, "Fetched user bind qq: $bindQQ") }

        maimaiFavList = try {
            CFQServer.apiFetchUserOption(authToken, "maimai_fav_list")
        } catch (e: Exception) {
            Log.i(tag, "User maimai fav list failed to fetch.")
            ""
        }.also { Log.i(tag, "Fetched user maimai fav list: $maimaiFavList") }

        chunithmFavList = try {
            CFQServer.apiFetchUserOption(authToken, "chunithm_fav_list")
        } catch (e: Exception) {
            Log.i(tag, "User chunithm fav list failed to fetch.")
            ""
        }.also { Log.i(tag, "Fetched user chunithm fav list: $chunithmFavList") }
    }

    suspend fun syncFavList(gameType: Int) {
        when (gameType) {
            0 -> {
                chunithmFavList = try {
                    CFQServer.apiFetchUserOption(authToken, "chunithm_fav_list")
                } catch (e: Exception) {
                    Log.i(tag, "User chunithm fav list failed to fetch.")
                    ""
                }.also { Log.i(tag, "Fetched user chunithm fav list: $chunithmFavList") }
            }
            1 -> {
                maimaiFavList = try {
                    CFQServer.apiFetchUserOption(authToken, "maimai_fav_list")
                } catch (e: Exception) {
                    Log.i(tag, "User maimai fav list failed to fetch.")
                    ""
                }.also { Log.i(tag, "Fetched user maimai fav list: $maimaiFavList") }
            }
        }
    }
}
