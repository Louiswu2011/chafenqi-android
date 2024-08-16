package com.nltv.chafenqi.view.home.log

import android.graphics.Color
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.nameplateChunithmBottomColor
import com.nltv.chafenqi.view.home.nameplateChunithmTopColor
import com.nltv.chafenqi.view.home.nameplateMaimaiBottomColor
import com.nltv.chafenqi.view.home.nameplateMaimaiTopColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.segmented
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLogPage(navController: NavController) {
    val model: HomeLogPageViewModel = viewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "出勤记录") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
        ) {
            HomeLogPageDataColumn(navController)
        }
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun HomeLogPageDataColumn(navController: NavController) {
    val model: HomeLogPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    val store = SettingsStore(context)
    val logDefaultPricePerRound by store.logDefaultPricePerRound.collectAsStateWithLifecycle(
        initialValue = 3f
    )

    LaunchedEffect(Unit) {
        model.updateInfo(CFQUser.mode)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = lazyListState
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    HomeLogLargeInfo(
                        title = "出勤天数",
                        source = uiState.totalDays.toString(),
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    HomeLogLargeInfo(
                        title = "游玩曲目数",
                        source = uiState.totalPlayCount.toString()
                    )
                }

                HomeLogLargeInfo(
                    title = "预估消费",
                    source = "￥${uiState.totalPlayCount * logDefaultPricePerRound}"
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeLogNormalInfo(
                    title = "平均游玩次数",
                    source = String.format(Locale.getDefault(), "%.2f", uiState.averagePlayPerDay),
                    modifier = Modifier.padding(end = 10.dp)
                )
                HomeLogNormalInfo(title = "近7次Rating平均增长", source = uiState.averageRatingGain)
            }
        }

        // TODO: Check fix on github
        item { HomeLogPageDataChart() }

        item {
            Text(
                text = "出勤记录",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
        items(
            count = uiState.logSize,
            key = { index ->
                if (CFQUser.mode == 0) uiState.chuLogs[index].date.epochSeconds else uiState.maiLogs[index].date.epochSeconds
            }
        ) {
            TextButton(
                onClick = {
                    if (CFQUser.mode == 0) {
                        navController.navigate(HomeNavItem.Home.route + "/log/chunithm/${it}")
                    } else if (CFQUser.mode == 1) {
                        navController.navigate(HomeNavItem.Home.route + "/log/maimai/${it}")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        if (CFQUser.mode == 0) {
                            Text(
                                text = uiState.chuLogs[it].date.toLocalDateTime(TimeZone.currentSystemDefault())
                                    .format(model.logEntryDateTimeFormatter)
                            )
                            Text(text = "${uiState.chuLogs[it].recentEntries.size}条记录")
                        } else if (CFQUser.mode == 1) {
                            Text(
                                text = uiState.maiLogs[it].date.toLocalDateTime(TimeZone.currentSystemDefault())
                                    .format(model.logEntryDateTimeFormatter)
                            )
                            Text(text = "${uiState.maiLogs[it].recentEntries.size}条记录")
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "In"
                    )
                }
            }
        }
    }
}

@Composable
fun HomeLogPageDataChart() {
    val model: HomeLogPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val chartZoomState = rememberVicoZoomState()
    val chartScrollState = rememberVicoScrollState()

    var chartMode by rememberSaveable {
        mutableIntStateOf(0)
    }

    LaunchedEffect(chartMode) {
        model.updateChart(gameMode = CFQUser.mode, chartMode = chartMode)
    }

    Column(
        modifier = Modifier.padding(top = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                AnimatedContent(targetState = chartMode, label = "animated chart title") {
                    when (it) {
                        0 -> Text(text = "游玩曲目数", fontWeight = FontWeight.Bold)
                        1 -> Text(text = "Rating", fontWeight = FontWeight.Bold)
                    }
                }
                Text(text = "历史数据")
            }
            Button(onClick = { chartMode = 1 - chartMode }) {
                Icon(
                    imageVector = Icons.Default.ChangeCircle,
                    contentDescription = "切换图标",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                AnimatedContent(targetState = chartMode, label = "animated chart title") {
                    when (it) {
                        0 -> Text(text = "切换到Rating")
                        1 -> Text(text = "切换到游玩次数")
                    }
                }
            }
        }
        Column {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        pointSpacing = 50.dp,
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            rememberLine(
                                remember {
                                    LineCartesianLayer.LineFill.double(
                                        topFill = fill(
                                            if (CFQUser.mode == 0) nameplateChunithmTopColor else nameplateMaimaiTopColor
                                        ),
                                        bottomFill = fill(
                                            if (CFQUser.mode == 0) nameplateChunithmBottomColor else nameplateMaimaiBottomColor
                                        )
                                    )
                                }
                            )
                        )
                    ),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { x, chartValues, _ -> chartValues.model.extraStore[model.labelKeyList][x.toInt()] }
                    ),
                    horizontalLayout = HorizontalLayout.segmented(),
                    marker = rememberDefaultCartesianMarker(
                        label = TextComponent(),
                        indicator = { _ -> ShapeComponent(
                            color = Color.GRAY,
                            shape = Shape.Pill,
                            shadow = Shadow(radiusDp = 5f)
                        ) },
                        labelPosition = DefaultCartesianMarker.LabelPosition.Top
                    )
                ),
                modelProducer = model.chartModelProducer,
            )
        }
    }
}

@Composable
fun HomeLogLargeInfo(title: String, source: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.then(modifier)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = source,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun HomeLogNormalInfo(title: String, source: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.then(modifier)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(text = source, style = MaterialTheme.typography.bodyMedium)
    }
}