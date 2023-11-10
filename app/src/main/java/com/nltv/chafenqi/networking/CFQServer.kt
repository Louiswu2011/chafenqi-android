package com.nltv.chafenqi.networking

import android.util.Log
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

        suspend fun authLogin(username: String, password: String): String {
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

        suspend fun apiChuithmMusicData(): String {
            val response = fetchFromServer(
                "GET",
                path = "api/chunithm/music_data"
            )
            return response.bodyAsText()
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


        private fun handleErrorCode(errorCode: String) {
            when (errorCode) {
                "MISMATCH" -> throw CredentialsMismatchException()
                "INVALID" -> throw InvalidTokenException()
                "NOT FOUND" -> throw UserNotFoundException()
                "EMPTY" -> throw EmptyUserDataException()
                else -> throw CFQServerSideException(errorCode = errorCode)
            }
        }
    }
}

class CredentialsMismatchException : Exception()
class InvalidTokenException : Exception()
class UserNotFoundException : Exception()
class EmptyUserDataException : Exception()
class CFQServerSideException(errorCode: String) : Exception(errorCode)