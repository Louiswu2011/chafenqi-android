package com.nltv.chafenqi.networking

import android.util.Log
import com.nltv.chafenqi.data.VersionData
import com.nltv.chafenqi.storage.user.CFQUserOptions
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CFQServer {
    companion object {
        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.NONE
            }
        }

        private suspend fun fetchFromServer(
            method: String,
            path: String,
            payload: HashMap<Any, Any>? = null,
            queries: Map<String, String>? = null,
            token: String? = null,
            shouldHandleErrorCode: Boolean = true
        ): HttpResponse {
            var response: HttpResponse
            when (method) {
                "GET" -> {
                    response = client.get("http://43.139.107.206:8083/$path") {
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

                "POST" -> {
                    response = client.post("http://43.139.107.206:8083/$path") {
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

                else -> {
                    throw Exception("Method not supported.")
                }
            }
            if (response.status.value != 200 && shouldHandleErrorCode) {
                handleErrorCode(response.bodyAsText())
            }
            return response
        }

        suspend fun apiFetchLatestVersion(): VersionData {
            return try {
                val deserializer = Json { ignoreUnknownKeys = true }
                val responseText = fetchFromServer(
                    "GET",
                    "api/stats/version"
                ).bodyAsText()
                deserializer.decodeFromString(responseText)
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to obtain latest version data, error: $e")
                VersionData()
            }
        }

        suspend fun authLogin(username: String, password: String): String {
            try {
                val response = fetchFromServer(
                    "POST",
                    "api/auth",
                    payload = hashMapOf(
                        "username" to username,
                        "password" to password
                    )
                )
                val errorCode = response.bodyAsText()
                val header = response.headers["Authorization"]?.substring(7)

                Log.i("CFQServer", "auth login: $errorCode")

                return header ?: ""
            } catch (e: Exception) {
                Log.e("CFQServer", "$e")
                return ""
            }
        }

        suspend fun authCheckUsername(username: String): Boolean {
            Log.i("CFQServer", "Checking username $username")
            val usernameCheckResponse = fetchFromServer(
                "POST",
                "api/checkUsername",
                payload = hashMapOf(
                    "username" to username
                )
            )
            return usernameCheckResponse.status.value == 200
        }

        suspend fun authRegister(username: String, password: String): Boolean {
            if (!authCheckUsername(username)) return false
            val registerResponse = fetchFromServer(
                "POST",
                "api/register",
                payload = hashMapOf(
                    "username" to username,
                    "password" to password
                )
            )
            return registerResponse.status.value == 200
        }

        suspend fun apiIsPremium(username: String): Boolean {
            Log.i("CFQServer", "Checking if user is premium...")
            val response = fetchFromServer(
                "POST",
                "api/isPremium",
                payload = hashMapOf(
                    "username" to username
                ),
                shouldHandleErrorCode = false
            )
            return response.status.value == 200
        }

        suspend fun apiCheckPremiumTime(username: String): Double {
            return try {
                val response = fetchFromServer(
                    "POST",
                    "api/premiumTime",
                    payload = hashMapOf(
                        "username" to username
                    )
                )
                response.bodyAsText().toDouble()
            } catch (e: Exception) {
                Log.e("CFQServer", "Failed to get premium time: ${e.localizedMessage}")
                0.0
            }
        }

        suspend fun apiRedeem(username: String, redeemCode: String): Boolean {
            Log.i("CFQServer", "Redeeming code $redeemCode")
            return try {
                val response = fetchFromServer(
                    "POST",
                    "api/redeemCode",
                    payload = hashMapOf(
                        "username" to username,
                        "code" to redeemCode
                    )
                )
                response.status.value == 200
            } catch (e: Exception) {
                false
            }
        }

        suspend fun apiFetchUserOptions(token: String): String {
            Log.i("CFQServer", "Fetching user options.")
            return try {
                val response = fetchFromServer(
                    "GET",
                    "api/user/options",
                    token = token
                )
                response.bodyAsText()
            } catch (e: Exception) {
                ""
            }
        }

        suspend fun apiUploadUserOptions(options: CFQUserOptions, token: String): Boolean {
            Log.i("CFQServer", "Uploading user options.")
            return try {
                val response = fetchFromServer(
                    "POST",
                    "api/user/options",
                    payload = hashMapOf(
                        "bindQQ" to options.bindQQ
                    ),
                    token = token
                )
                response.status.value == 200
            } catch (e: Exception) {
                false
            }
        }

        suspend fun apiChuithmMusicData(): String {
            val response = fetchFromServer(
                "GET",
                path = "api/chunithm/music_data"
            )
            return response.bodyAsText()
        }

        suspend fun apiIsUploading(gameType: Int, authToken: String): Boolean {
            val response = fetchFromServer(
                "GET",
                "api/user/isUploading",
                queries = mapOf("dest" to gameType.toString()),
                token = authToken,
                shouldHandleErrorCode = false
            )
            return response.status.value == 200
        }

        suspend fun apiHasTokenCache(gameType: Int, authToken: String): Boolean {
            val response = fetchFromServer(
                "GET",
                "api/user/hasCache",
                queries = mapOf("dest" to gameType.toString()),
                token = authToken,
                shouldHandleErrorCode = false
            )
            return response.status.value == 200
        }

        suspend fun apiTriggerQuickUpload(
            gameType: Int,
            shouldForward: Boolean,
            authToken: String
        ) {
            fetchFromServer(
                "POST",
                "api/quick_upload",
                payload = hashMapOf(
                    "dest" to gameType,
                    "forwarding" to if (shouldForward) 1 else 0
                ),
                token = authToken
            )
        }

        suspend fun apiMaimai(contentTag: String, authToken: String): String = fetchFromServer(
            "GET",
            path = "api/maimai/$contentTag",
            token = authToken
        ).bodyAsText()

        suspend fun apiChunithm(contentTag: String, authToken: String): String = fetchFromServer(
            "GET",
            path = "api/chunithm/$contentTag",
            token = authToken
        ).bodyAsText()

        suspend fun fishFetchToken(authToken: String) = fetchFromServer(
            "GET",
            "fish/fetch_token",
            token = authToken
        ).bodyAsText()


        suspend fun fishUploadToken(authToken: String, fishToken: String): Boolean {
            val response = fetchFromServer(
                "POST",
                "fish/upload_token",
                payload = hashMapOf(
                    "token" to fishToken
                ),
                token = authToken
            )
            return response.status.value == 200
        }

        suspend fun statUploadTime(mode: Int): String {
            val response = fetchFromServer(
                "POST",
                "api/stats/upload_time",
                payload = hashMapOf(
                    "type" to mode
                )
            )
            return response.bodyAsText()
        }

        suspend fun statCheckUpload(authToken: String): List<Int> {
            val response = fetchFromServer(
                "GET",
                "api/stats/upload_status",
                token = authToken
            )
            val dict = Json.decodeFromString<Map<String, Int>>(response.bodyAsText())
            return listOf(dict["chu"] ?: -1, dict["mai"] ?: -1)
        }

        suspend fun statSponsorList(): List<String> {
            return try {
                val responseText = fetchFromServer(
                    "GET",
                    "api/stats/sponsor"
                ).bodyAsText()
                Json.decodeFromString<List<String>>(responseText).distinct().reversed()
            } catch (e: Exception) {
                Log.e("CFQServer", "Error fetching sponsor list: ${e.localizedMessage}")
                listOf()
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
class CFQServerSideException(errorCode: String) : Exception(errorCode)