package com.nltv.chafenqi.view.home.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.log.ChunithmLogData
import com.nltv.chafenqi.storage.log.MaimaiLogData
import com.nltv.chafenqi.storage.user.CFQUser
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

class HomeLogPageViewModel : ViewModel() {
    data class HomeLogPageUiState(
        val totalDays: Int = 0,
        val totalPlayCount: Int = 0,
        val averagePlayPerDay: Float = 0f,
        val averageRatingGain: String = "",
        val logSize: Int = 0,
        val maiLogs: List<MaimaiLogData.MaimaiDayData> = listOf(),
        val chuLogs: List<ChunithmLogData.ChunithmDayData> = listOf(),
    )

    private val maimaiLogData = CFQUser.maimai.log
    private val chunithmLogData = CFQUser.chunithm.log

    private val chartDateTimeFormatter = LocalDateTime.Format {
        monthNumber()
        char('-')
        dayOfMonth()
    }

    val logEntryDateTimeFormatter = LocalDateTime.Format {
        year()
        char('/')
        monthNumber()
        char('/')
        dayOfMonth()
    }

    private var homeLogPageUiState = MutableStateFlow(HomeLogPageUiState())
    var uiState = homeLogPageUiState.asStateFlow()

    val chartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    val labelKeyList = ExtraStore.Key<List<String>>()

    fun updateInfo(mode: Int) {
        when (mode) {
            0 -> updateChunithmInfo()
            1 -> updateMaimaiInfo()
        }
    }

    fun updateChart(gameMode: Int, chartMode: Int) {
        when (gameMode) {
            0 -> updateChunithmChart(chartMode)
            1 -> updateMaimaiChart(chartMode)
        }
    }

    private fun updateMaimaiInfo() {
        if (maimaiLogData == null) return
        val totalPlayed = maimaiLogData.records.sumOf { it.recentEntries.size }
        val recentSevenEntries = maimaiLogData.records.take(7)
        val latestRating = recentSevenEntries.firstOrNull()?.latestDeltaEntry?.rating ?: 0
        val oldRating = recentSevenEntries.lastOrNull()?.latestDeltaEntry?.rating ?: 0
        viewModelScope.launch {
            homeLogPageUiState.update {
                it.copy(
                    totalDays = maimaiLogData.dayPlayed,
                    totalPlayCount = totalPlayed,
                    averagePlayPerDay = totalPlayed / maimaiLogData.dayPlayed.toFloat(),
                    averageRatingGain = String.format(
                        Locale.getDefault(),
                        "%.3f",
                        (latestRating - oldRating).toFloat() / recentSevenEntries.size
                    ),
                    logSize = maimaiLogData.records.size,
                    maiLogs = maimaiLogData.records
                )
            }
        }
    }

    private fun updateMaimaiChart(chartMode: Int) {
        when (chartMode) {
            0 -> updateMaimaiPlayCountChart()
            1 -> updateMaimaiRatingChart()
        }
    }

    private fun updateMaimaiPlayCountChart() {
        if (maimaiLogData == null) return
        viewModelScope.launch {
            chartModelProducer.runTransaction {
                lineSeries {
                    series(maimaiLogData.records.map { it.recentEntries.size })
                }
                extras { it[labelKeyList] = maimaiLogData.records.map { record -> record.date.toLocalDateTime(TimeZone.currentSystemDefault()).format(chartDateTimeFormatter) } }
            }
        }
    }

    private fun updateMaimaiRatingChart() {
        if (maimaiLogData == null) return
        viewModelScope.launch {
            chartModelProducer.runTransaction {
                lineSeries {
                    series(maimaiLogData.records.map { it.latestDeltaEntry.rating })
                }
                extras { it[labelKeyList] = maimaiLogData.records.map { record -> record.date.toLocalDateTime(TimeZone.currentSystemDefault()).format(chartDateTimeFormatter) } }
            }
        }
    }

    private fun updateChunithmInfo() {
        if (chunithmLogData == null) return
        val totalPlayed = chunithmLogData.records.sumOf { it.recentEntries.size }
        val recentSevenEntries = chunithmLogData.records.takeLast(7)
        val latestRating = recentSevenEntries.firstOrNull()?.latestDeltaEntry?.rating ?: 0.0
        val oldRating = recentSevenEntries.lastOrNull()?.latestDeltaEntry?.rating ?: 0.0
        viewModelScope.launch {
            homeLogPageUiState.update {
                it.copy(
                    totalDays = chunithmLogData.dayPlayed,
                    totalPlayCount = totalPlayed,
                    averagePlayPerDay = totalPlayed / chunithmLogData.dayPlayed.toFloat(),
                    averageRatingGain = String.format(
                        Locale.getDefault(),
                        "%.3f",
                        (latestRating - oldRating) / recentSevenEntries.size
                    ),
                    logSize = chunithmLogData.records.size,
                    chuLogs = chunithmLogData.records
                )
            }
        }
    }

    private fun updateChunithmChart(chartMode: Int) {
        when (chartMode) {
            0 -> updateChunithmPlayCountChart()
            1 -> updateChunithmRatingChart()
        }
    }

    private fun updateChunithmPlayCountChart() {
        if (chunithmLogData == null) return
        viewModelScope.launch {
            chartModelProducer.runTransaction {
                lineSeries {
                    series(chunithmLogData.records.map { it.recentEntries.size })
                }
                extras { it[labelKeyList] = chunithmLogData.records.map { record -> record.date.toLocalDateTime(TimeZone.currentSystemDefault()).format(chartDateTimeFormatter) } }
            }
        }
    }

    private fun updateChunithmRatingChart() {
        if (chunithmLogData == null) return
        viewModelScope.launch {
            chartModelProducer.runTransaction {
                lineSeries {
                    series(chunithmLogData.records.map { it.latestDeltaEntry.rating })
                }
                extras { it[labelKeyList] = chunithmLogData.records.map { record -> record.date.toLocalDateTime(TimeZone.currentSystemDefault()).format(chartDateTimeFormatter) } }
            }
        }
    }
}