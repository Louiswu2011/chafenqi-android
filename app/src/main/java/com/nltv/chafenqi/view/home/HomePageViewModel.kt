package com.nltv.chafenqi.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.nltv.chafenqi.BuildConfig
import com.nltv.chafenqi.CFQUserStateViewModel
import com.nltv.chafenqi.cacheStore
import com.nltv.chafenqi.data.rank.ChunithmFirstRank
import com.nltv.chafenqi.data.rank.ChunithmRatingRank
import com.nltv.chafenqi.data.rank.ChunithmTotalPlayedRank
import com.nltv.chafenqi.data.rank.ChunithmTotalScoreRank
import com.nltv.chafenqi.data.rank.LeaderboardRank
import com.nltv.chafenqi.data.rank.MaimaiFirstRank
import com.nltv.chafenqi.data.rank.MaimaiRatingRank
import com.nltv.chafenqi.data.rank.MaimaiTotalPlayedRank
import com.nltv.chafenqi.data.rank.MaimaiTotalScoreRank
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.storage.user.ChunithmRecentLineup
import com.nltv.chafenqi.storage.user.MaimaiRecentLineup
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.math.ceil

data class HomePageUiState(
    val mode: Int = 0,

    val nickname: String = "",
    val rating: String = "",
    val playCount: String = "",
    val canNavigateToRecentList: Boolean = true,
    val canNavigateToRatingList: Boolean = true,
    val canOpenMaimaiInfo: Boolean = false,
    val canOpenChunithmInfo: Boolean = false,
    val shouldShowRatingBar: Boolean = true,

    val nameplateUpdateTime: String = "",

    val maiRecentLineup: List<MaimaiRecentLineup> = listOf(),
    val maiPastRating: String = "",
    val maiNewRating: String = "",
    val maiIndicatorsCount: Int = 0,
    val maiPastIndicatorsCount: Int = 0,
    val maiNewIndicatorsCount: Int = 0,
    val maiPastRatingList: List<MaimaiBestScoreEntry> = listOf(),
    val maiNewRatingList: List<MaimaiBestScoreEntry> = listOf(),
    val maiCurrentSelectedRatingEntry: MaimaiBestScoreEntry = MaimaiBestScoreEntry(),
    val maiCurrentSelectedRatingEntryType: String = "",
    val maiCurrentSelectedRatingEntryRank: Int = -1,

    val chuRecentLineup: List<ChunithmRecentLineup> = listOf(),
    val chuMaxRating: String = "",
    val chuBestRating: String = "",
    val chuRecentRating: String = "",
    val chuIndicatorsCount: Int = 0,
    val chuBestRatingList: List<ChunithmRatingEntry> = listOf(),
    val chuCurrentSelectedRatingEntry: ChunithmRatingEntry = ChunithmRatingEntry(),

    val currentSelectedIndicatorIndex: Int = -1,
    val indicatorHeights: MutableList<Dp> = mutableListOf(),

    val isLoadingLeaderboardBar: Boolean = true,
    val maiLeaderboardRank: MutableList<LeaderboardRank?> = mutableListOf(null, null, null, null),
    val chuLeaderboardRank: MutableList<LeaderboardRank?> = mutableListOf(null, null, null, null),

    val isLogEmpty: Boolean = true,
    val logLastPlayedTime: String = "",
    val logLastPlayedCount: String = "",
    val logLastPlayedDuration: String = "",
    val logLastPlayedAverageScore: String = ""
)

class HomePageViewModel : ViewModel() {
    private val tag = "HomePageViewModel"
    val user = CFQUser

    private val _uiState = MutableStateFlow(HomePageUiState())
    val uiState: StateFlow<HomePageUiState> = _uiState.asStateFlow()

    private val maiIndicatorsCount = user.maimai.aux.pastBest.size + user.maimai.aux.newBest.size
    private val chuIndicatorsCount = user.chunithm.aux.bestList.size

    private var previousIndex = -1

    var showNewVersionDialog by mutableStateOf(false)
    var latestVersionCode = ""
    var latestBuildNumber = 0
    private val currentVersionString = BuildConfig.VERSION_NAME
    val currentVersionCode = currentVersionString.split(" ")[0]
    val currentBuildNumber = currentVersionString.split(" ")[1]
        .removePrefix("(")
        .removeSuffix(")")
        .toIntOrNull() ?: 0

    var isLoaded: Boolean = false

    suspend fun checkUpdates() {
        viewModelScope.launch {
            val versionData = CFQServer.apiFetchLatestVersion()
            val fullVersionString = BuildConfig.VERSION_NAME
            Log.i(
                tag,
                "Current version: $currentVersionCode (${currentBuildNumber}), latest version: ${versionData.androidVersionCode} (${versionData.androidBuild})"
            )
            if (!versionData.isLatest(currentVersionCode, currentBuildNumber)) {
                latestVersionCode = versionData.androidVersionCode
                latestBuildNumber = versionData.androidBuild.toIntOrNull() ?: 0
                showNewVersionDialog = true
            }
        }
    }

    fun update() {
        _uiState.update { currentState ->
            when (user.mode) {
                1 -> {
                    currentState.copy(
                        mode = 1,
                        nickname = user.maimai.info.nickname,
                        rating = user.maimai.info.rating.toString(),
                        playCount = user.maimai.info.playCount.toString(),
                        canNavigateToRecentList = user.maimai.recent.isNotEmpty(),
                        canNavigateToRatingList = user.maimai.aux.pastBest.isNotEmpty() && user.maimai.aux.newBest.isNotEmpty(),
                        nameplateUpdateTime = user.maimai.aux.updateTime,
                        maiRecentLineup = user.maimai.aux.recommendList.take(3),
                        maiPastRating = user.maimai.aux.pastRating.toString(),
                        maiNewRating = user.maimai.aux.newRating.toString(),
                        maiIndicatorsCount = this.maiIndicatorsCount,
                        maiPastIndicatorsCount = user.maimai.aux.pastBest.size,
                        maiNewIndicatorsCount = user.maimai.aux.newBest.size,
                        maiPastRatingList = user.maimai.aux.pastBest,
                        maiNewRatingList = user.maimai.aux.newBest,
                        indicatorHeights = MutableList(this.maiIndicatorsCount) { 20.dp },
                        maiCurrentSelectedRatingEntry = if (user.maimai.aux.pastBest.isNotEmpty()) user.maimai.aux.pastBest.first() else MaimaiBestScoreEntry(),
                        maiCurrentSelectedRatingEntryType = "旧曲",
                        maiCurrentSelectedRatingEntryRank = 1,
                        currentSelectedIndicatorIndex = 0,
                        canOpenMaimaiInfo = user.isPremium && !user.maimai.isExtraEmpty,
                        shouldShowRatingBar = user.maimai.aux.newBest.isNotEmpty() || user.maimai.aux.pastBest.isNotEmpty()
                    )
                }

                0 -> {
                    currentState.copy(
                        mode = 0,
                        nickname = user.chunithm.info.nickname,
                        rating = String.format("%.2f", user.chunithm.info.rating),
                        playCount = user.chunithm.info.playCount.toString(),
                        nameplateUpdateTime = user.chunithm.aux.updateTime,
                        canNavigateToRecentList = user.chunithm.recent.isNotEmpty(),
                        canNavigateToRatingList = user.chunithm.rating.isNotEmpty(),
                        chuRecentLineup = user.chunithm.aux.recommendList.take(3),
                        chuMaxRating = String.format("%.2f", user.chunithm.info.maxRating),
                        chuBestRating = String.format("%.2f", user.chunithm.aux.bestRating),
                        chuRecentRating = String.format("%.2f", user.chunithm.aux.recentRating),
                        chuIndicatorsCount = user.chunithm.aux.bestList.size,
                        chuBestRatingList = user.chunithm.aux.bestList,
                        indicatorHeights = MutableList(this.chuIndicatorsCount) { 20.dp },
                        chuCurrentSelectedRatingEntry = if (user.chunithm.aux.bestList.isNotEmpty()) user.chunithm.aux.bestList.first() else ChunithmRatingEntry(),
                        currentSelectedIndicatorIndex = 0,
                        canOpenChunithmInfo = user.isPremium && !user.chunithm.isExtraEmpty,
                        shouldShowRatingBar = user.chunithm.aux.bestList.isNotEmpty()
                    )
                }

                else -> {
                    currentState
                }
            }
        }
    }

    fun switchGame() {
        user.mode = 1 - user.mode
        update()
    }

    fun switchGame(game: Int) {
        // Log.i(tag, "Switching game mode from ${user.mode} to ${1 - user.mode}...")
        user.mode = game
        update()
    }

    fun navigateToRecentList(navController: NavController) {
        if (!_uiState.value.canNavigateToRecentList) return

        navController.navigate(HomeNavItem.Home.route + "/recent")
    }

    fun navigateToRecentLog(navController: NavController, item: MaimaiRecentLineup) {
        val index = user.maimai.recent.indexOf(item.entry)
        if (!_uiState.value.canNavigateToRecentList || index < 0) return

        navController.navigate(HomeNavItem.Home.route + "/recent/maimai/$index")
    }

    fun navigateToRecentLog(navController: NavController, item: ChunithmRecentLineup) {
        val index = user.chunithm.recent.indexOf(item.entry)
        if (!_uiState.value.canNavigateToRecentList || index < 0) return

        navController.navigate(HomeNavItem.Home.route + "/recent/chunithm/$index")
    }

    fun updateRatingIndicators(touchPoint: Offset, maxWidth: Dp, density: Density) {
        with(density) {
            val indicatorCount =
                if (user.mode == 1) _uiState.value.maiIndicatorsCount else _uiState.value.chuIndicatorsCount
            val x = touchPoint.x.toDp()

            val gridWidth = maxWidth / (indicatorCount * 2 - 1)
            val position = minOf(maxOf(ceil(x / gridWidth / 2).toInt() - 1, 0), indicatorCount - 1)

            val maiPastSize = user.maimai.aux.pastBest.size
            val maiPastLastIndex = user.maimai.aux.pastBest.lastIndex
            val maiPosition = if (position > maiPastLastIndex) position - maiPastSize else position

            if (position != previousIndex) {
                if (user.mode == 1) {
                    _uiState.update { currentValue ->
                        currentValue.copy(
                            currentSelectedIndicatorIndex = position,
                            maiCurrentSelectedRatingEntry = if (position > maiPastLastIndex) user.maimai.aux.newBest[maiPosition] else user.maimai.aux.pastBest[maiPosition],
                            maiCurrentSelectedRatingEntryType = if (position > maiPastLastIndex) "新曲" else "旧曲",
                            maiCurrentSelectedRatingEntryRank = (if (position > maiPastLastIndex) position - maiPastSize else position) + 1
                        )
                    }
                } else if (user.mode == 0) {
                    _uiState.update { currentValue ->
                        currentValue.copy(
                            currentSelectedIndicatorIndex = position,
                            chuCurrentSelectedRatingEntry = user.chunithm.aux.bestList[position]
                        )
                    }
                }
                previousIndex = position
            }
        }
    }

    fun resetRatingIndicators() {
        _uiState.update { currentValue ->
            currentValue.copy(
                indicatorHeights = MutableList(currentValue.indicatorHeights.size) { 10.dp }
            )
        }
    }

    fun getIndicatorHeight(
        index: Int,
        currentPosition: Int,
        density: Density,
        isTouching: Boolean
    ): Float {
        return with(density) {
            if (isTouching) {
                when (currentPosition) {
                    index -> 22.dp.toPx()
                    index - 1, index + 1 -> 16.dp.toPx()
                    index - 2, index + 2 -> 12.dp.toPx()
                    else -> 10.dp.toPx()
                }
            } else {
                10.dp.toPx()
            }
        }
    }

    fun refreshUserData(userState: CFQUserStateViewModel, context: Context) {
        userState.isRefreshing = true
        viewModelScope.launch {
            async {
                userState.loadMaimaiData(context)
                userState.loadChunithmData(context)
            }.invokeOnCompletion {
                userState.isRefreshing = false
                update()
                Log.i(tag, "Refresh completed.")
            }
        }
    }

    suspend fun saveCredentialsToCache(context: Context): Boolean {
        val store = context.cacheStore
        val tokenKey = stringPreferencesKey("cachedToken")
        val usernameKey = stringPreferencesKey("cachedUsername")

        return try {
            store.edit {
                it[tokenKey] = user.token
                it[usernameKey] = user.username
            }
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to save credentials to cache.")
            false
        }
    }

    fun navigateToMusicEntry(bestEntry: MaimaiBestScoreEntry, navController: NavController) {
        if (CFQPersistentData.Maimai.musicList.isEmpty()) return

        val maiMusicEntryIndex =
            CFQPersistentData.Maimai.musicList.indexOf(bestEntry.associatedMusicEntry)
        if (maiMusicEntryIndex < 0) return

        navController.navigate(HomeNavItem.SongList.route + "/maimai/$maiMusicEntryIndex")
    }

    fun navigateToMusicEntry(ratingEntry: ChunithmRatingEntry, navController: NavController) {
        if (CFQPersistentData.Chunithm.musicList.isEmpty()) return

        val chuMusicEntryIndex =
            CFQPersistentData.Chunithm.musicList.indexOf(ratingEntry.associatedMusicEntry)
        if (chuMusicEntryIndex < 0) return

        navController.navigate(HomeNavItem.SongList.route + "/chunithm/$chuMusicEntryIndex")
    }

    fun fetchLeaderboardRanks() {
        when (user.mode) {
            0 -> {
                if (!_uiState.value.chuLeaderboardRank.all { it != null }) {
                    // Load
                    _uiState.update { currentValue ->
                        currentValue.copy(isLoadingLeaderboardBar = true)
                    }
                    viewModelScope.launch {
                        val ratingRank =
                            CFQServer.apiFetchUserLeaderboardRank<ChunithmRatingRank>(user.token)
                        val totalScoreRank =
                            CFQServer.apiFetchUserLeaderboardRank<ChunithmTotalScoreRank>(user.token)
                        val totalPlayedRank =
                            CFQServer.apiFetchUserLeaderboardRank<ChunithmTotalPlayedRank>(user.token)
                        val firstRank =
                            CFQServer.apiFetchUserLeaderboardRank<ChunithmFirstRank>(user.token)

                        _uiState.update { currentValue ->
                            currentValue.copy(
                                isLoadingLeaderboardBar = false,
                                chuLeaderboardRank = mutableListOf(
                                    ratingRank,
                                    totalScoreRank,
                                    totalPlayedRank,
                                    firstRank
                                )
                            )
                        }
                    }
                }
            }

            1 -> {
                if (!_uiState.value.maiLeaderboardRank.all { it != null }) {
                    // Load
                    _uiState.update { currentValue ->
                        currentValue.copy(isLoadingLeaderboardBar = true)
                    }
                    viewModelScope.launch {
                        val ratingRank =
                            CFQServer.apiFetchUserLeaderboardRank<MaimaiRatingRank>(user.token)
                        val totalScoreRank =
                            CFQServer.apiFetchUserLeaderboardRank<MaimaiTotalScoreRank>(user.token)
                        val totalPlayedRank =
                            CFQServer.apiFetchUserLeaderboardRank<MaimaiTotalPlayedRank>(user.token)
                        val firstRank =
                            CFQServer.apiFetchUserLeaderboardRank<MaimaiFirstRank>(user.token)

                        _uiState.update { currentValue ->
                            currentValue.copy(
                                isLoadingLeaderboardBar = false,
                                maiLeaderboardRank = mutableListOf(
                                    ratingRank,
                                    totalScoreRank,
                                    totalPlayedRank,
                                    firstRank
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun updateLog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLogEmpty = true
                )
            }
        }
        when (user.mode) {
            0 -> {
                if (user.chunithm.log == null) return
                if (user.chunithm.log!!.records.isEmpty()) return
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            logLastPlayedTime = user.chunithm.log?.records?.first()?.date?.toLocalDateTime(
                                TimeZone.currentSystemDefault()
                            )?.format(LocalDateTime.Format {
                                year()
                                char('-')
                                monthNumber()
                                char('-')
                                dayOfMonth()
                            }) ?: "",
                            logLastPlayedCount = user.chunithm.log?.records?.first()?.recentEntries?.size?.toString()
                                ?: "",
                            logLastPlayedDuration = user.chunithm.log?.records?.first()?.durationString ?: "",
                            logLastPlayedAverageScore = String.format(Locale.getDefault(), "%.0f", user.chunithm.log?.records?.first()?.averageScore ?: 0),
                            isLogEmpty = false
                        )
                    }
                }
            }
            1 -> {
                if (user.maimai.log == null) return
                if (user.maimai.log!!.records.isEmpty()) return
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            logLastPlayedTime = user.maimai.log?.records?.first()?.date?.toLocalDateTime(
                                TimeZone.currentSystemDefault()
                            )?.format(LocalDateTime.Format {
                                year()
                                char('-')
                                monthNumber()
                                char('-')
                                dayOfMonth()
                            }) ?: "",
                            logLastPlayedCount = user.maimai.log?.records?.first()?.recentEntries?.size?.toString()
                                ?: "",
                            logLastPlayedDuration = user.maimai.log?.records?.first()?.durationString ?: "",
                            logLastPlayedAverageScore = String.format(
                                Locale.getDefault(),
                                "%.4f",
                                user.maimai.log?.records?.first()?.averageScore ?: 0
                            ) + "%",
                            isLogEmpty = false
                        )
                    }
                }
            }
        }
    }
}
