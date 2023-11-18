package com.nltv.chafenqi.view.songlist.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.LineDrawer
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.line.SolidLineShader
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.line.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongRecordPage(navController: NavController, mode: Int, index: Int, levelIndex: Int) {
    val model = viewModel<SongRecordPageViewModel>().also {
        it.update(mode, index, levelIndex)
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { androidx.compose.material3.Text(text = "历史记录") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column (
            Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            SongRecordChart()
        }
    }
}

@Composable
fun SongRecordChart() {
    val model: SongRecordPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    val chartData = when (model.mode) {
        0 -> {
            listOf(LineChartData(
                points = uiState.chuHistoryEntries.map { LineChartData.Point(it.score.toFloat(), it.timestamp.toString()) },
                startAtZero = false,
                lineDrawer = SolidLineDrawer()
            ))
        }
        else -> {
            listOf(LineChartData(
                points = uiState.maiHistoryEntries.map { LineChartData.Point(it.achievements, it.timestamp.toString()) },
                startAtZero = false,
                lineDrawer = SolidLineDrawer()
            ))
        }
    }

    LineChart(
        linesChartData = chartData,
        animation = simpleChartAnimation(),
        pointDrawer = FilledCircularPointDrawer(),
        lineShader = SolidLineShader(),
        xAxisDrawer = SimpleXAxisDrawer(),
        yAxisDrawer = SimpleYAxisDrawer()
    )
}

@Preview(showBackground = true)
@Composable
fun ChartPreview() {

}