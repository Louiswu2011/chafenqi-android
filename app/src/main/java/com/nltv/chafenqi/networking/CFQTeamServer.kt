package com.nltv.chafenqi.networking

import android.util.Log
import com.nltv.chafenqi.extension.toGameTypeString
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamBulletinBoardEntry
import com.nltv.chafenqi.model.team.TeamCreatePayload
import com.nltv.chafenqi.model.team.TeamInfo
import com.nltv.chafenqi.model.team.TeamUpdateCoursePayload
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object CFQTeamServer {
    val decoder = Json {
        ignoreUnknownKeys = true
    }

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
                    connectTimeout(10, TimeUnit.SECONDS)
                    readTimeout(10, TimeUnit.SECONDS)
                    writeTimeout(10, TimeUnit.SECONDS)
                }
            }
        }

    private suspend fun fetchFromServer(
        method: HttpMethod,
        path: String,
        payload: Any? = null,
        queries: Map<String, String>? = null,
        token: String? = null,
    ): HttpResponse {
        val url = "${CFQServer.defaultPath}/api/team/$path"
        val response = client.request(url) {
            this.method = method
            token?.also {
                this.headers.append("Authorization", "Bearer $it")
            }
            queries?.also { q ->
                url {
                    q.forEach {
                        parameters.append(it.key, it.value)
                    }
                }
            }
            payload?.also {
                contentType(ContentType.Application.Json)
                setBody(it)
            }
        }

        Log.d("CFQTeamServer", "Response from ${method.value} $url:")
        Log.d("CFQTeamServer", "Response status: ${response.status}")
        Log.d("CFQTeamServer", "Response headers: ${response.headers}")
        Log.d("CFQTeamServer", "Response body: ${response.bodyAsText().take(50)}")

        return response
    }

    private suspend fun fetchFromTeam(
        token: String,
        game: Int,
        teamId: Int,
        method: HttpMethod,
        path: String,
        payload: Any? = null,
        queries: Map<String, String>? = null,
    ) = fetchFromServer(
        method = method,
        path = "${game.toGameTypeString()}/$teamId/$path",
        payload = payload,
        queries = queries,
        token = token,
    )

    suspend fun fetchAllTeams(authToken: String, game: Int): List<TeamBasicInfo> {
        try {
            val response = fetchFromServer(
                method = HttpMethod.Get,
                path = game.toGameTypeString(),
                token = authToken
            )
            return decoder.decodeFromString(response.bodyAsText())
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to fetch all teams for game $game: ${e.localizedMessage}")
            return emptyList()
        }
    }

    suspend fun fetchCurrentTeam(authToken: String, game: Int): Int? {
        try {
            val response = fetchFromServer(
                method = HttpMethod.Get,
                path = "${game.toGameTypeString()}/current",
                token = authToken,
            )

            return response.bodyAsText().toIntOrNull()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to fetch current team: ${e.localizedMessage}")
            return null
        }
    }

    suspend fun fetchTeamInfo(authToken: String, game: Int, teamId: Int): TeamInfo? {
        try {
            val response = fetchFromServer(
                method = HttpMethod.Get,
                path = "${game.toGameTypeString()}/$teamId",
                token = authToken,
            )
            val json = response.bodyAsText()
            return decoder.decodeFromString<TeamInfo>(json)
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to fetch team info: $e")
            return null
        }
    }

    suspend fun createTeam(authToken: String, payload: TeamCreatePayload): String {
        try {
            val response = fetchFromServer(
                method = HttpMethod.Post,
                path = "${payload.game.toGameTypeString()}/create",
                payload = payload,
                token = authToken,
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to create team: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun applyForTeam(authToken: String, game: Int, teamId: Int, message: String): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "apply",
                payload = message,
                token = authToken,
                teamId = teamId,
                game = game,
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to apply for team: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun leaveTeam(authToken: String, game: Int, teamId: Int): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Delete,
                path = "leave",
                token = authToken,
                teamId = teamId,
                game = game,
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to leave team: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun getTeamBulletinBoard(authToken: String, game: Int, teamId: Int): List<TeamBulletinBoardEntry>? {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Get,
                path = "bulletin",
                token = authToken,
                teamId = teamId,
                game = game,
            )
            return decoder.decodeFromString(response.bodyAsText())
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to get team bulletin board: ${e.localizedMessage}")
            return null
        }
    }

    suspend fun addTeamBulletinBoardEntry(authToken: String, game: Int, teamId: Int, message: String): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "bulletin",
                payload = """
                    {
                        "message": "$message"
                    }
                """.trimIndent(),
                token = authToken,
                teamId = teamId,
                game = game,
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to add team bulletin board entry: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun deleteTeamBulletinBoardEntry(authToken: String, game: Int, teamId: Int, entryId: Int): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Delete,
                path = "bulletin",
                token = authToken,
                teamId = teamId,
                game = game,
                queries = mapOf("id" to entryId.toString()),
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to delete team bulletin board entry: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun adminAcceptMember(authToken: String, game: Int, teamId: Int, pendingMemberId: Long): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/member/accept",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "userId": $pendingMemberId
                    }
                """.trimIndent(),
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to accept pending member: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun adminRejectMember(authToken: String, game: Int, teamId: Int, pendingMemberId: Long): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/member/reject",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "userId": $pendingMemberId
                    }
                """.trimIndent(),
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to reject pending member: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun adminKickMember(authToken: String, game: Int, teamId: Int, memberId: Long): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Delete,
                path = "admin/member/kick",
                token = authToken,
                teamId = teamId,
                game = game,
                queries = mapOf("userId" to memberId.toString()),
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to kick member: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun adminTransferOwnership(authToken: String, game: Int, teamId: Int, newLeaderUserId: Long): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Put,
                path = "admin/member/transfer",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "newLeaderUserId": $newLeaderUserId
                    }
                """.trimIndent(),
            )

            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to transfer ownership: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun adminRotateTeamCode(authToken: String, game: Int, teamId: Int): Boolean {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/update/code",
                token = authToken,
                teamId = teamId,
                game = game,
            )
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to rotate team code: ${e.localizedMessage}")
            return false
        }
    }

    suspend fun adminUpdateTeamName(authToken: String, game: Int, teamId: Int, newName: String): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/update/name",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "displayName": "$newName"
                    }
                """.trimIndent(),
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to update team name: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun adminUpdateTeamStyle(authToken: String, game: Int, teamId: Int, newStyle: String): Boolean {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/update/style",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "style": "$newStyle"
                    }
                """.trimIndent(),
            )
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to update team style: ${e.localizedMessage}")
            return false
        }
    }

    suspend fun adminUpdateTeamRemarks(authToken: String, game: Int, teamId: Int, newRemarks: String): Boolean {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/update/remarks",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "remarks": "$newRemarks"
                    }
                """.trimIndent(),
            )
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to update team remarks: ${e.localizedMessage}")
            return false
        }
    }

    suspend fun adminUpdateTeamPromotable(authToken: String, game: Int, teamId: Int, promotable: Boolean): Boolean {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/update/promotable",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "promotable": ${if (promotable) "true" else "false"}
                    }
                """.trimIndent(),
            )
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to update team promotable: ${e.localizedMessage}")
            return false
        }
    }

    suspend fun adminUpdateTeamCourse(authToken: String, game: Int, teamId: Int, newCourse: TeamUpdateCoursePayload): String {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/update/course",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = newCourse,
            )
            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to update team course: ${e.localizedMessage}")
            return "未知错误，请联系开发者。"
        }
    }

    suspend fun adminSetPinnedMessage(authToken: String, game: Int, teamId: Int, pinnedMessageId: Int): Boolean {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Post,
                path = "admin/update/pinned",
                token = authToken,
                teamId = teamId,
                game = game,
                payload = """
                    {
                        "id": $pinnedMessageId
                    }
                """.trimIndent(),
            )
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to set pinned message: ${e.localizedMessage}")
            return false
        }
    }

    suspend fun adminResetPinnedMessage(authToken: String, game: Int, teamId: Int): Boolean {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Delete,
                path = "admin/update/pinned",
                token = authToken,
                teamId = teamId,
                game = game,
            )
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to reset pinned message: ${e.localizedMessage}")
            return false
        }
    }

    suspend fun adminDisbandTeam(authToken: String, game: Int, teamId: Int): Boolean {
        try {
            val response = fetchFromTeam(
                method = HttpMethod.Delete,
                path = "admin/disband",
                token = authToken,
                teamId = teamId,
                game = game,
            )
            return response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CFQTeamServer", "Failed to disband team: ${e.localizedMessage}")
            return false
        }
    }
}