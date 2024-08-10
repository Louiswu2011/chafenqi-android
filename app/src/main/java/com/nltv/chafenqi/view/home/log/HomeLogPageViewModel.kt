package com.nltv.chafenqi.view.home.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.log.MaimaiLogData
import com.nltv.chafenqi.storage.user.CFQUser
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class HomeLogPageViewModel : ViewModel() {
    data class HomeLogPageUiState(
        val chartAnchorToggle: Boolean = true,
        val totalDays: Int = 0,
        val totalPlayCount: Int = 0,
        val estimatedCost: Float = 0f,
        val averagePlayPerDay: Float = 0f,
        val averageRatingGain: String = "",
        val logSize: Int = 0,
        val maiLogs: List<MaimaiLogData.MaimaiDayData> = listOf(),
        // TODO: Add chuLogs
    )

    private val maimaiLogData = CFQUser.maimai.log
    private var homeLogPageUiState = MutableStateFlow(HomeLogPageUiState())
    var uiState = homeLogPageUiState.asStateFlow()

    val chartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()

    fun toggleChartAnchor() {
        viewModelScope.launch {
            homeLogPageUiState.update {
                it.copy(
                    chartAnchorToggle = !it.chartAnchorToggle
                )
            }
        }
    }

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
        val recentSevenEntries = maimaiLogData.records.takeLast(7)
        val latestRating = recentSevenEntries.lastOrNull()?.latestDeltaEntry?.rating ?: 0
        val oldRating = recentSevenEntries.firstOrNull()?.latestDeltaEntry?.rating ?: 0
        viewModelScope.launch {
            homeLogPageUiState.update {
                it.copy(
                    totalDays = maimaiLogData.dayPlayed,
                    totalPlayCount = totalPlayed,
                    estimatedCost = totalPlayed * 3f, // TODO: Add customizable per pc cost
                    averagePlayPerDay = totalPlayed / maimaiLogData.dayPlayed.toFloat(),
                    averageRatingGain = String.format(Locale.getDefault(), "%.3f", (latestRating - oldRating).toFloat() / recentSevenEntries.size),
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
            }
        }
    }

    private fun updateChunithmInfo() {

    }

    private fun updateChunithmChart(chartMode: Int) {
        when (chartMode) {
            0 -> updateChunithmPlayCountChart()
            1 -> updateChunithmRatingChart()
        }
    }

    private fun updateChunithmPlayCountChart() {

    }

    private fun updateChunithmRatingChart() {

    }
}