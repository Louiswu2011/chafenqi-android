package com.nltv.chafenqi.view.songlist.record

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nltv.chafenqi.extension.rememberMarker
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.axisLineComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.component.marker.markerComponent
import com.patrykandpatrick.vico.compose.component.overlayingComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.marker.Marker

@Composable
fun SongRecordPage() {

}

@Preview(showBackground = true)
@Composable
fun SongRecordPagePreview() {
    val chartModel = entryModelOf(1, 5, 10, 13, 20, 21, 18, 13, 7, 0)

    Chart(
        chart = LineChart(),
        model = chartModel,
        startAxis = rememberStartAxis(
            label = axisLabelComponent(color = MaterialTheme.colors.error)
        ),
        bottomAxis = rememberBottomAxis(),
        marker = rememberMarker()
    )
}