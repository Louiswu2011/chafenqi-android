package com.nltv.chafenqi.networking

import android.util.Log
import com.nltv.chafenqi.data.ChunithmMusicStat
import com.nltv.chafenqi.data.Comment
import com.nltv.chafenqi.data.VersionData
import com.nltv.chafenqi.data.leaderboard.ChunithmDiffLeaderboard
import com.nltv.chafenqi.data.leaderboard.ChunithmFirstLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.ChunithmRatingLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.ChunithmTotalPlayedLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.ChunithmTotalScoreLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiDiffLeaderboard
import com.nltv.chafenqi.data.leaderboard.MaimaiFirstLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiRatingLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiTotalPlayedLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiTotalScoreLeaderboardItem
import com.nltv.chafenqi.model.user.UserInfo
import com.nltv.chafenqi.util.AppAnnouncement
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class CFQServer {
    companion object {
        var defaultPath = "http://192.168.1.151:8998"

        fun setDefaultServerPath(path: String) {
            defaultPath = path
        }

        private val decoder = Json { ignoreUnknownKeys = true }

        val client =
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json()
                }
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.NONE
                }
                engine {
                    config {
                        connectTimeout(30, TimeUnit.SECONDS)
                        readTimeout(30, TimeUnit.SECONDS)
                        writeTimeout(30, TimeUnit.SECONDS)
                    }
                }
            }

        suspend fun fetchFromServer(
            method: String,
            path: String,
            payload: HashMap<String, Any>? = null,
            queries: Map<String, String>? = null,
            token: String? = null,
            shouldHandleErrorCode: Boolean = true,
        ): HttpResponse {
            try {
                val response: HttpResponse
                when (method) {
                    "GET" -> {
                        response =
                            client.get("$defaultPath/$path") {
                                queries?.also { q ->
                                    url { u ->
                                        q.forEach {
                                            u.parameters.append(it.key, it.value)
                                        }
                                    }
                                }
                                token?.also {
                                    this.headers.append("Authorization", "Bearer $it")
                                }
                            }
                    }

                    "POST" -> {
                        response =
                            client.post("$defaultPath/$path") {
                                accept(ContentType.Any)
                                payload?.also {
                                    this.contentType(ContentType.Application.Json)
                                    this.setBody(it)
                                }
                                token?.also {
                                    this.headers.append("Authorization", "Bearer $it")
                                }
                            }
                    }

                    "DELETE" -> {
                        response =
                            client.delete("$defaultPath/$path") {
                                accept(ContentType.Any)
                                queries?.also { q ->
                                    url { u ->
                                        q.forEach {
                                            u.parameters.append(it.key, it.value)
                                        }
                                    }
                                }
                                token?.also {
                                    this.headers.append("Authorization", "Bearer $it")
                                }
                            }
                    }

                    else -> {
                        throw Exception("Method not supported.")
                    }
                }

                Log.d("CFQServer", "Response from $path:")
                Log.d("CFQServer", "Response status: ${response.status}")
                Log.d("CFQServer", "Response headers: ${response.headers}")
                Log.d("CFQServer", "Response body: ${response.bodyAsText().take(50)}")
                return response
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch from server, error: $e")
                throw e
            }
        }

        suspend fun apiFetchLatestVersion(): VersionData =
            try {
                val deserializer = Json { ignoreUnknownKeys = true }
                val responseText =
                    fetchFromServer(
                        "GET",
                        "api/stat/version/app/android",
                    ).bodyAsText()
                deserializer.decodeFromString(responseText)
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to obtain latest version data, error: $e")
                VersionData.current
            }

        suspend fun authLogin(
            username: String,
            password: String,
        ): String {
            val response =
                fetchFromServer(
                    "POST",
                    "api/auth/login",
                    payload =
                        hashMapOf(
                            "username" to username,
                            "password" to password,
                        ),
                )

            val body = response.bodyAsText()
            Log.i("CFQServer", "Auth Login response: ${response.status.value}")
            return body
        }

        private suspend fun authCheckUsername(username: String): Boolean {
            Log.i("CFQServer", "Checking username $username")
            try {
                val usernameCheckResponse =
                    fetchFromServer(
                        "POST",
                        "api/auth/register/check-availability",
                        payload =
                            hashMapOf(
                                "username" to username,
                            ),
                    )
                return usernameCheckResponse.status.value == 200
            } catch (_: Exception) {
                return false
            }
        }

        suspend fun authRegister(
            username: String,
            password: String,
        ): Boolean {
            try {
                if (!authCheckUsername(username)) return false
                val registerResponse =
                    fetchFromServer(
                        "POST",
                        "api/auth/register",
                        payload =
                            hashMapOf(
                                "username" to username,
                                "password" to password,
                            ),
                    )
                return registerResponse.status.value == 200
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to register user, error: $e")
                return false
            }
        }

        suspend fun apiUserInfo(token: String): UserInfo? {
            try {
                Log.i("CFQServer", "Fetching user info.")
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/info",
                        token = token,
                    )

                return decoder.decodeFromString<UserInfo>(response.bodyAsText())
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to get user info, error: $e")
                return null
            }
        }

        suspend fun apiCheckPremiumTime(token: String): Long =
            try {
                val response =
                    fetchFromServer(
                        "POST",
                        "api/user/info",
                        token = token
                    )
                decoder.decodeFromString<UserInfo>(response.bodyAsText()).premiumUntil
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to get premium time: ${e.localizedMessage}")
                0L
            }

        suspend fun apiRedeem(
            token: String,
            redeemCode: String,
        ): Boolean {
            Log.i("CFQServer", "Redeeming code $redeemCode")
            return try {
                val response =
                    fetchFromServer(
                        "POST",
                        "api/user/redeem",
                        payload =
                            hashMapOf(
                                "code" to redeemCode,
                            ),
                        token = token
                    )
                response.status.value == 200
            } catch (e: Exception) {
                false
            }
        }

        suspend inline fun <reified T> apiFetchUserOption(
            token: String,
            param: String,
            type: String,
        ): T? {
            Log.i("CFQServer", "Fetching user option: $param.")
            return try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/properties",
                        token = token,
                        queries =
                            mapOf(
                                "param" to param,
                                "type" to type,
                            ),
                    )
                val value: T? = response.bodyAsText() as? T
                return value
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch user option: $param, type: $type, error: ${e.localizedMessage}")
                null
            }
        }

        suspend inline fun <reified T> apiUploadUserOption(
            token: String,
            param: String,
            value: T,
        ): Boolean {
            Log.i("CFQServer", "Uploading $param option.")
            return try {
                val response =
                    fetchFromServer(
                        "POST",
                        "api/user/properties",
                        payload =
                            hashMapOf(
                                "param" to param,
                                "value" to value.toString(),
                            ),
                        token = token,
                    )
                response.status.value == 200
            } catch (e: Exception) {
                false
            }
        }

        suspend fun apiFetchFavMusic(
            token: String,
            gameType: Int,
        ): String? {
            Log.i("CFQServer", "Fetching game $gameType favorites.")
            return try {
                val response =
                    fetchFromServer(
                        method = "GET",
                        "api/user/favorite",
                        token = token,
                        queries =
                            hashMapOf(
                                "game" to gameType.toString(),
                            ),
                    )
                response.bodyAsText()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch game $gameType favorites: ${e.localizedMessage}")
                null
            }
        }

        suspend fun apiAddFavMusic(
            token: String,
            gameType: Int,
            musicId: String,
        ): String? {
            Log.i("CFQServer", "Adding game $gameType music $musicId to favorites.")
            return try {
                val response =
                    fetchFromServer(
                        method = "POST",
                        "api/user/favorite",
                        token = token,
                        payload =
                            hashMapOf(
                                "game" to gameType.toString(),
                                "musicId" to musicId,
                            ),
                    )
                response.bodyAsText()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to add game $gameType music $musicId to favorites: ${e.localizedMessage}")
                null
            }
        }

        suspend fun apiRemoveFavMusic(
            token: String,
            gameType: Int,
            musicId: String,
        ): String? {
            Log.i("CFQServer", "Removing game $gameType music $musicId to favorites.")
            return try {
                val response =
                    fetchFromServer(
                        method = "DELETE",
                        "api/user/favorite",
                        token = token,
                        queries =
                            mapOf(
                                "game" to gameType.toString(),
                                "musicId" to musicId,
                            ),
                    )
                response.bodyAsText()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to remove game $gameType music $musicId to favorites: ${e.localizedMessage}")
                null
            }
        }

        suspend fun apiFetchUserCurrentQQ(authToken: String): String =
            try {
                val response =
                    fetchFromServer(
                        method = "GET",
                        "api/user/bind",
                        token = authToken,
                    )
                response.bodyAsText()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch user bind qq: ${e.localizedMessage}")
                ""
            }

        suspend fun apiUserBindQQ(
            authToken: String,
            bindQQ: String,
        ): Boolean =
            try {
                val response =
                    fetchFromServer(
                        method = "POST",
                        "api/user/bind",
                        payload =
                            hashMapOf(
                                "qq" to bindQQ,
                            ),
                        token = authToken,
                    )
                response.status.value == 200
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to bind qq: ${e.localizedMessage}")
                false
            }

        // TODO: Add UI implementation for unbinding QQ
        suspend fun apiUserUnbindQQ(authToken: String): Boolean =
            try {
                val response =
                    fetchFromServer(
                        method = "DELETE",
                        "api/user/bind",
                        token = authToken,
                    )
                response.status.value == 200
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to unbind qq: ${e.localizedMessage}")
                false
            }

        // TODO: Adapt new data models
        suspend inline fun <reified T> apiFetchUserLeaderboardRank(authToken: String): T? {
            try {
                val className = T::class.simpleName ?: ""
                val gameType =
                    when {
                        className.startsWith("Maimai") -> 0
                        className.startsWith("Chunithm") -> 1
                        else -> {
                            -1
                        }
                    }
                if (gameType < 0) {
                    return null
                }

                val leaderboardTypeString =
                    when {
                        className.contains("Rating") -> "rating"
                        className.contains("Played") -> "total-played"
                        className.contains("Score") -> "total-score"
                        className.contains("First") -> "first"
                        else -> ""
                    }
                if (leaderboardTypeString.isEmpty()) {
                    return null
                }

                val gameTypeString =
                    when (gameType) {
                        0 -> "maimai"
                        1 -> "chunithm"
                        else -> ""
                    }
                val response =
                    client.get("$defaultPath/api/user/leaderboard/$gameTypeString/$leaderboardTypeString") {
                        accept(ContentType.Any)
                        this.headers.append("Authorization", "Bearer $authToken")
                    }
                if (response.status.value != 200) {
                    return null
                }
                return Json.decodeFromString<T>(response.bodyAsText())
            } catch (e: Exception) {
                Log.e(
                    "CFQServer",
                    "Failed to fetch ${T::class.simpleName ?: "error"} leaderboard rank.\n$e",
                )
                return null
            }
        }

        // TODO: Maybe deprecate this
        suspend fun apiFetchAnnouncement(): List<AppAnnouncement> {
            Log.i("CFQServer", "Fetching app announcements.")
            return try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/announcement",
                        queries =
                            hashMapOf(
                                "device" to "Android",
                            ),
                    )
                if (response.status.value != 200) {
                    return emptyList()
                }

                val json = response.bodyAsText()
                return Json
                    .decodeFromString<List<AppAnnouncement>>(json)
                    .sortedByDescending { it.issueDate }
            } catch (e: Exception) {
                emptyList()
            }
        }

        suspend fun apiChuithmMusicData(): String {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        path = "api/resource/chunithm/song-list",
                    )
                return response.bodyAsText()
            } catch (e: Exception) {
                return ""
            }
        }

        suspend fun apiMaimaiMusicData(): String {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        path = "api/resource/maimai/song-list",
                    )
                return response.bodyAsText()
            } catch (e: Exception) {
                return ""
            }
        }

        suspend fun apiMaimaiGenreData(): String {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        path = "api/resource/maimai/genre-list",
                    )
                return response.bodyAsText()
            } catch (e: Exception) {
                return ""
            }
        }

        suspend fun apiMaimaiVersionData(): String {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        path = "api/resource/maimai/version-list",
                    )
                return response.bodyAsText()
            } catch (e: Exception) {
                return ""
            }
        }

        // TODO: Change to new status API
        suspend fun apiIsUploading(
            gameType: Int,
            authToken: String,
        ): Boolean {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/isUploading",
                        queries = mapOf("dest" to gameType.toString()),
                        token = authToken,
                        shouldHandleErrorCode = false,
                    )
                return response.status.value == 200
            } catch (e: Exception) {
                return false
            }
        }

        // TODO: Add server side implementation
        suspend fun apiHasTokenCache(
            gameType: Int,
            authToken: String,
        ): Boolean {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/hasCache",
                        queries = mapOf("dest" to gameType.toString()),
                        token = authToken,
                        shouldHandleErrorCode = false,
                    )
                return response.status.value == 200
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to check token cache: ${e.localizedMessage}")
                return false
            }
        }

        // TODO: Add server side implementation
        suspend fun apiTriggerQuickUpload(
            gameType: Int,
            authToken: String,
        ) {
            try {
                fetchFromServer(
                    "POST",
                    "api/quick_upload",
                    payload =
                        hashMapOf(
                            "dest" to gameType,
                        ),
                    token = authToken,
                )
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to trigger quick upload: ${e.localizedMessage}")
            }
        }

        suspend fun apiMaimai(
            contentTag: String,
            authToken: String,
        ): String {
            try {
                return fetchFromServer(
                    "GET",
                    path = "api/user/maimai/$contentTag",
                    token = authToken,
                ).bodyAsText()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch maimai $contentTag music data: $e")
                return ""
            }
        }

        suspend fun apiChunithm(
            contentTag: String,
            authToken: String,
        ): String {
            try {
                return fetchFromServer(
                    "GET",
                    path = "api/user/chunithm/$contentTag",
                    token = authToken,
                ).bodyAsText()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch chunithm music $contentTag data: $e")
                return ""
            }
        }

        suspend fun fishFetchToken(authToken: String): String {
            try {
                return apiFetchUserOption<String>(authToken, "fish_token", "string") ?: ""
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch fish token: ${e.localizedMessage}")
                return ""
            }
        }

        suspend fun fishUploadToken(
            authToken: String,
            fishToken: String,
        ): Boolean {
            try {
                return apiUploadUserOption(authToken, "fish_token", fishToken)
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to upload fish token: ${e.localizedMessage}")
                return false
            }
        }

        suspend fun statUploadTime(mode: Int): String {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/stat/upload-time",
                        queries =
                            mapOf(
                                "type" to mode.toString(),
                            ),
                    )
                return response.bodyAsText()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to get upload time: ${e.localizedMessage}")
                return "0"
            }
        }

        // TODO: Change to new data model
        suspend fun statCheckUpload(authToken: String): List<Int> {
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/upload-status",
                        token = authToken,
                    )
                val dict = Json.decodeFromString<Map<String, Int>>(response.bodyAsText())
                return listOf(dict["chu"] ?: -1, dict["mai"] ?: -1)
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to check upload status: ${e.localizedMessage}")
                return listOf(-1, -1)
            }
        }

        // TODO: Add server side implementation
        suspend fun statSponsorList(): List<String> =
            try {
                val responseText =
                    fetchFromServer(
                        "GET",
                        "api/stat/sponsor",
                    ).bodyAsText()
                Json.decodeFromString<List<String>>(responseText).distinct().reversed()
            } catch (e: Exception) {
                Log.e("CFQServer", "Error fetching sponsor list: ${e.localizedMessage}")
                listOf()
            }

        // TODO: Add server side implementation
        suspend fun statResourceVersion(tag: String): String =
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/stat/version/resource",
                        queries =
                            mapOf(
                                "tag" to tag,
                            ),
                    ).bodyAsText()
                response
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to get music list version, $e, defaulting to empty.")
                ""
            }

        // TODO: Add server side implementation
        suspend fun apiChunithmMusicStat(
            musicId: Int,
            difficulty: Int,
        ): ChunithmMusicStat {
            Log.i(
                "CFQServer",
                "Fetching chunithm music stat for music $musicId, difficulty $difficulty",
            )
            return try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/chunithm/stats",
                        queries =
                            mapOf(
                                "index" to musicId.toString(),
                                "diff" to difficulty.toString(),
                            ),
                        shouldHandleErrorCode = false,
                    )
                Json.decodeFromString<ChunithmMusicStat>(response.bodyAsText())
            } catch (e: Exception) {
                Log.e(
                    "CFQServer",
                    "Failed to fetch chunithm music stat for music $musicId, difficulty $difficulty.\n$e",
                )
                ChunithmMusicStat()
            }
        }

        suspend fun apiMaimaiLeaderboard(
            authToken: String,
            musicId: Int,
            type: String,
            difficulty: Int,
        ): MaimaiDiffLeaderboard {
            Log.i(
                "CFQServer",
                "Fetching maimai leaderboard for music $musicId, difficulty $difficulty",
            )
            return try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/maimai/leaderboard",
                        queries =
                            mapOf(
                                "music_id" to musicId.toString(),
                                "type" to type,
                                "level_index" to difficulty.toString(),
                            ),
                        token = authToken,
                        shouldHandleErrorCode = false,
                    )
                Json.decodeFromString<MaimaiDiffLeaderboard>(response.bodyAsText())
            } catch (e: Exception) {
                Log.e(
                    "CFQServer",
                    "Failed to fetch maimai leaderboard for music $musicId, type $type, difficulty $difficulty.\n$e",
                )
                emptyList()
            }
        }

        suspend fun apiChunithmLeaderboard(
            authToken: String,
            musicId: Int,
            difficulty: Int,
        ): ChunithmDiffLeaderboard {
            Log.i(
                "CFQServer",
                "Fetching chunithm leaderboard for music $musicId, difficulty $difficulty",
            )
            return try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/chunithm/leaderboard",
                        queries =
                            mapOf(
                                "music_id" to musicId.toString(),
                                "level_index" to difficulty.toString(),
                            ),
                        token = authToken,
                        shouldHandleErrorCode = false,
                    )
                Json.decodeFromString<ChunithmDiffLeaderboard>(response.bodyAsText())
            } catch (e: Exception) {
                Log.e(
                    "CFQServer",
                    "Failed to fetch chunithm leaderboard for music $musicId, difficulty $difficulty.\n$e",
                )
                emptyList()
            }
        }

        suspend inline fun <reified T> apiTotalLeaderboard(
            authToken: String,
            gameType: Int,
        ): List<T> {
            val gameName = if (gameType == 0) "chunithm" else "maimai"
            val typeString =
                when (T::class) {
                    ChunithmRatingLeaderboardItem::class, MaimaiRatingLeaderboardItem::class -> {
                        "rating"
                    }

                    ChunithmTotalScoreLeaderboardItem::class, MaimaiTotalScoreLeaderboardItem::class -> {
                        "total-score"
                    }

                    ChunithmTotalPlayedLeaderboardItem::class, MaimaiTotalPlayedLeaderboardItem::class -> {
                        "total-played"
                    }

                    ChunithmFirstLeaderboardItem::class, MaimaiFirstLeaderboardItem::class -> {
                        "first"
                    }

                    else -> {
                        ""
                    }
                }
            if (typeString.isEmpty()) {
                return emptyList()
            }

            return try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/user/$gameName/leaderboard/$typeString",
                        token = authToken,
                        shouldHandleErrorCode = false,
                    )
                Json.decodeFromString<List<T>>(response.bodyAsText())
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch $typeString leaderboard for ${gameName}\n$e")
                emptyList()
            }
        }

        suspend fun apiFetchComment(
            authToken: String,
            gameType: String,
            musicId: Int,
        ): List<Comment> =
            try {
                val response =
                    fetchFromServer(
                        "GET",
                        "api/comment/$gameType",
                        queries =
                            mapOf(
                                "musicId" to musicId.toString(),
                            ),
                        token = authToken,
                    )
                decoder.decodeFromString<List<Comment>>(response.bodyAsText())
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch comments for game $gameType music $musicId: ${e.localizedMessage}")
                emptyList()
            }

        suspend fun apiPostComment(
            authToken: String,
            gameType: String,
            musicId: Int,
            replyId: Int,
            content: String,
        ): Boolean {
            try {
                val response =
                    fetchFromServer(
                        "POST",
                        "api/comment/$gameType",
                        payload =
                            hashMapOf(
                                "content" to content,
                                "musicId" to musicId.toString(),
                                "timestamp" to
                                    Clock.System
                                        .now()
                                        .epochSeconds
                                        .toString(),
                            ),
                        token = authToken,
                    )
                return response.status.value == 200
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to post comment: ${e.localizedMessage}")
                return false
            }
        }

        suspend fun apiDeleteComment(
            authToken: String,
            gameType: String,
            commentId: Int,
        ): Boolean {
            try {
                val response =
                    fetchFromServer(
                        "DELETE",
                        "api/comment/$gameType",
                        queries =
                            mapOf(
                                "comment_id" to commentId.toString(),
                            ),
                        token = authToken,
                    )
                return response.status.value == 200
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to delete comment: ${e.localizedMessage}")
                return false
            }
        }

        // TODO: Add server side implementation
        suspend fun apiFetchB30Image(
            authToken: String
        ): ByteArray? {
            try {
                val response =
                    fetchFromServer(
                        method = "GET",
                        path = "api/user/chunithm/image/b30",
                        token = authToken,
                    )
                return response.body()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch user image: ${e.localizedMessage}")
                return null
            }
        }

        suspend fun apiFetchB50Image(authToken: String): ByteArray? {
            try {
                val response =
                    fetchFromServer(
                        method = "GET",
                        path = "api/user/maimai/image/b50",
                        token = authToken,
                    )
                return response.body()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to fetch b50 image: ${e.localizedMessage}")
                return null
            }
        }

        private fun handleErrorCode(errorCode: String) {
            when (errorCode) {
                "MISMATCH" -> throw CredentialsMismatchException()
                "INVALID" -> throw InvalidTokenException()
                "NOT FOUND" -> throw UserNotFoundException()
                "EMPTY" -> throw EmptyUserDataException()
                "NOT UNIQUE" -> throw UsernameOccupiedException()
                else -> throw CFQServerSideException(errorCode = errorCode)
            }
        }
    }
}

class CredentialsMismatchException : Exception()

class InvalidTokenException : Exception()

class UserNotFoundException : Exception()

class EmptyUserDataException : Exception()

class UsernameOccupiedException : Exception()

class CFQServerSideException(
    errorCode: String,
) : Exception(errorCode)
