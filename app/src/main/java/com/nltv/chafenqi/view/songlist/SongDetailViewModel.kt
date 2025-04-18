package com.nltv.chafenqi.view.songlist

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Image
import com.nltv.chafenqi.data.Comment
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.model.user.chunithm.UserChunithmBestScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiBestScoreEntry
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.util.ChartImageGrabber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val maimaiDifficultyTitles = listOf("Basic", "Advanced", "Expert", "Master", "Re:Master")
val chunithmDifficultyTitles =
    listOf("Basic", "Advanced", "Expert", "Master", "Ultima", "World's End")

val maimaiDifficultyColors = listOf(
    Color(red = 128, green = 216, blue = 98),
    Color(red = 242, green = 218, blue = 71),
    Color(red = 237, green = 127, blue = 132),
    Color(red = 176, green = 122, blue = 238),
    Color(red = 206, green = 164, blue = 251),
)
val chunithmDifficultyColors = listOf(
    Color(red = 73, green = 166, blue = 137),
    Color(red = 237, green = 123, blue = 33),
    Color(red = 205, green = 85, blue = 77),
    Color(red = 171, green = 104, blue = 249),
    Color(red = 68, green = 63, blue = 63),
    Color.White
)

data class SongDetailUiState(
    var chartUrls: List<String> = emptyList(),
    var expertAvailable: Boolean = false,
    var chartImages: MutableList<Image?> = mutableListOf(null, null, null),
    var chartExpanded: Boolean = false,
    var loved: Boolean = false,
    var syncing: Boolean = false,
    var comments: List<Comment> = listOf(),
    var coverUrl: String = "",
    var title: String = "",
    var artist: String = "",
    var constants: List<String> = listOf(),
    var bpm: String = "",
    var version: String = "",
    var genre: String = "",
    var maiDiffInfos: List<MaimaiDifficultyInfo> = listOf(),
    var chuDiffInfos: List<ChunithmDifficultyInfo> = listOf(),
)

class SongDetailViewModel : ViewModel() {
    private val tag = this::class.java.canonicalName

    val user = CFQUser

    var maiMusic: MaimaiMusicEntry? = null
    var chuMusic: ChunithmMusicEntry? = null

    var index: Int = 0

    val difficultyColors: List<Color>
        get() = if (user.mode == 1) maimaiDifficultyColors else chunithmDifficultyColors

    private val _uiState = MutableStateFlow(SongDetailUiState())
    val uiState = _uiState.asStateFlow()

    val images = mutableListOf<Drawable?>(null, null, null)

    fun update(mode: Int, index: Int) {
        this.index = index
        if (mode == 0 && CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
            chuMusic = CFQPersistentData.Chunithm.musicList.getOrNull(index)
            if (chuMusic == null) return

            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        coverUrl = "${CFQServer.defaultPath}/api/resource/chunithm/cover?musicId=${chuMusic?.musicId}",
                        title = chuMusic?.title ?: "",
                        artist = chuMusic?.artist ?: "",
                        constants = chuMusic?.charts?.constants?.map { String.format("%.1f", it) }
                            ?.filterNot { it == "0.0" } ?: listOf(),
                        bpm = chuMusic?.bpm?.toString() ?: "",
                        version = chuMusic?.from ?: "",
                        genre = chuMusic?.genre ?: "",
                        chuDiffInfos = if (chuMusic!!.isWE) {
                            listOf(ChunithmDifficultyInfo(it.title, 5, chuMusic!!))
                        } else {
                            chuMusic!!.charts.indexedList.mapIndexedNotNull { difficultyIndex, chart ->
                                if (chart.enabled) {
                                    ChunithmDifficultyInfo(
                                        _uiState.value.title,
                                        difficultyIndex,
                                        chuMusic!!
                                    )
                                } else null
                            }
                        }
                    )
                }
            }

            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        loved = user.remoteOptions.chunithmFavList.contains(
                            chuMusic?.musicId?.toString() ?: ""
                        ),
                        comments = CFQServer.apiFetchComment(
                            authToken = user.token,
                            gameType = "chunithm",
                            musicId = chuMusic!!.musicId
                        ).sortedByDescending { comment -> comment.timestamp }
                    )
                }
            }


        } else if (mode == 1 && CFQPersistentData.Maimai.musicList.isNotEmpty()) {
            maiMusic = CFQPersistentData.Maimai.musicList.getOrNull(index)
            if (maiMusic == null) return

            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        coverUrl = maiMusic?.coverId?.toMaimaiCoverPath() ?: "",
                        title = maiMusic?.title ?: "",
                        artist = maiMusic?.basicInfo?.artist ?: "",
                        constants = maiMusic?.constants?.map { String.format("%.1f", it) }
                            ?: listOf(),
                        bpm = maiMusic?.basicInfo?.bpm?.toString() ?: "",
                        version = maiMusic?.basicInfo?.from ?: "",
                        genre = maiMusic?.basicInfo?.genre ?: "",
                        maiDiffInfos = maiMusic?.charts?.mapIndexed { difficultyIndex, chart ->
                            MaimaiDifficultyInfo(
                                it.title,
                                difficultyIndex,
                                maiMusic ?: MaimaiMusicEntry()
                            )
                        } ?: emptyList<MaimaiDifficultyInfo>()
                    )
                }
            }

            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        loved = user.remoteOptions.maimaiFavList.contains(maiMusic?.musicId.toString()),
                        comments = CFQServer.apiFetchComment(
                            authToken = user.token,
                            gameType = "maimai",
                            musicId = maiMusic!!.musicId.toInt()
                        ).sortedByDescending { comment -> comment.timestamp }
                    )
                }
            }
        }
    }

    fun updateChartUrls(diffIndex: Int) {
        viewModelScope.launch {
            val urls = ChartImageGrabber.getChartPreviewImageUrls(
                chartInfo = ChartImageGrabber.ChartInfo(
                    musicId = chuMusic?.musicId?.toString() ?: "-1",
                    diffIndex = diffIndex
                )
            )
            _uiState.update {
                it.copy(
                    chartUrls = urls
                )
            }
        }
    }

    fun updateChartImage(index: Int, drawable: Image) {
        _uiState.update {
            it.copy(
                chartImages = it.chartImages.apply {
                    set(index, drawable)
                }
            )
        }
    }

    fun toggleLoved(currentState: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    syncing = true,
                    loved = !it.loved
                )
            }
            val currentString = if (currentState) removeLoved() else addLoved()
            _uiState.update {
                it.copy(
                    syncing = false,
                    loved = if (currentString == null) currentState else !currentState
                )
            }
            if (currentString != null) {
                if (user.mode == 0) {
                    user.remoteOptions.chunithmFavList = currentString
                } else if (user.mode == 1) {
                    user.remoteOptions.maimaiFavList = currentString
                }
            } else {
                Log.i("SongDetailViewModel", "Failed to toggle loved")
            }
        }
    }

    private suspend fun addLoved(): String? {
        return when (user.mode) {
            0 -> {
                if (chuMusic == null) return null
                val expectedString =
                    if (user.remoteOptions.chunithmFavList.isEmpty()) "${chuMusic!!.musicId}" else user.remoteOptions.chunithmFavList + ",${chuMusic!!.musicId}"
                val actualString =
                    CFQServer.apiAddFavMusic(user.token, 0, chuMusic!!.musicId.toString())
                        ?: return null
                if (expectedString == actualString) {
                    actualString
                } else {
                    Log.e("SongDetailViewModel", "Failed to add loved, expected: $expectedString, actual: $actualString")
                    null
                }
            }

            1 -> {
                if (maiMusic == null) return null
                val expectedString =
                    if (user.remoteOptions.maimaiFavList.isEmpty()) "${maiMusic!!.musicId}" else user.remoteOptions.maimaiFavList + ",${maiMusic!!.musicId}"
                val actualString = CFQServer.apiAddFavMusic(user.token, 1, maiMusic!!.musicId.toString())
                    ?: return null
                if (expectedString == actualString) {
                    actualString
                } else {
                    Log.e("SongDetailViewModel", "Failed to add loved, expected: $expectedString, actual: $actualString")
                    null
                }
            }

            else -> null
        }
    }

    private suspend fun removeLoved(): String? {
        return when (user.mode) {
            0 -> {
                if (chuMusic == null) return null
                val expectedString = user.remoteOptions.chunithmFavList
                    .split(',')
                    .filter { it != chuMusic!!.musicId.toString() }
                    .joinToString(",")
                val actualString =
                    CFQServer.apiRemoveFavMusic(user.token, 0, chuMusic!!.musicId.toString())
                        ?: return null
                if (expectedString != actualString) null else actualString
            }

            1 -> {
                if (maiMusic == null) return null
                val expectedString = user.remoteOptions.maimaiFavList
                    .split(',')
                    .filter { it != maiMusic!!.musicId.toString() }
                    .joinToString(",")
                val actualString = CFQServer.apiRemoveFavMusic(user.token, 1, maiMusic!!.musicId.toString())
                    ?: return null
                if (expectedString != actualString) null else actualString
            }

            else -> null
        }
    }

    fun toggleExpand() {
        _uiState.update {
            it.copy(
                chartExpanded = !it.chartExpanded
            )
        }
    }
}

class MaimaiDifficultyInfo(
    title: String,
    difficultyIndex: Int,
    musicEntry: MaimaiMusicEntry
) {
    var levelIndex: Int = difficultyIndex
    var difficultyName: String = ""

    var color: Color = maimaiDifficultyColors[difficultyIndex]
    var constant: String = ""
    var charter: String = ""
    var bestScore: String = "暂未游玩"
    var bestEntry: UserMaimaiBestScoreEntry? = null
    var hasRecentEntry: Boolean = false

    init {
        difficultyName = maimaiDifficultyTitles[difficultyIndex]
        constant = String.format("%.1f", musicEntry.constants[difficultyIndex])
        charter = musicEntry.charts[difficultyIndex].charter
        bestEntry = CFQUser.maimai.best.firstOrNull {
            it.associatedMusicEntry == musicEntry && it.levelIndex == difficultyIndex
        }
        if (bestEntry != null) {
            bestScore = String.format("%.4f", bestEntry!!.achievements) + "%"
        }
        hasRecentEntry =
            CFQUser.maimai.recent.firstOrNull { it.associatedMusicEntry.musicId == musicEntry.musicId && it.levelIndex == levelIndex } != null
    }
}

class ChunithmDifficultyInfo(
    title: String,
    difficultyIndex: Int,
    musicEntry: ChunithmMusicEntry
) {
    var levelIndex: Int = difficultyIndex
    var difficultyName: String = ""

    var color: Color = chunithmDifficultyColors[difficultyIndex]
    var constant: String = ""
    var charter: String = ""
    var bestScore: String = "暂未游玩"
    var bestEntry: UserChunithmBestScoreEntry? = null
    var hasRecentEntry: Boolean = false

    init {
        difficultyName = chunithmDifficultyTitles[difficultyIndex]
        constant = String.format("%.1f", musicEntry.charts.constants[difficultyIndex])
        charter = musicEntry.charts.charters[difficultyIndex] ?: "-"
        bestEntry = CFQUser.chunithm.best.firstOrNull {
            it.associatedMusicEntry == musicEntry && it.levelIndex == difficultyIndex
        }
        if (bestEntry != null) {
            bestScore = bestEntry!!.score.toString()
        }
        hasRecentEntry =
            CFQUser.chunithm.recent.firstOrNull { it.associatedMusicEntry.musicId == musicEntry.musicId && it.levelIndex == levelIndex } != null
    }
}