package com.nltv.chafenqi.glance

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.launch
import java.util.Locale

class CFQAppWidget: GlanceAppWidget() {
    companion object {
        private val nickname = stringPreferencesKey("nickname")
        private val rating = stringPreferencesKey("rating")
        private val playCount = stringPreferencesKey("playCount")
        private val lastUpdate = stringPreferencesKey("lastUpdate")

        private val lastPlayedImage = stringPreferencesKey("lastPlayedImage")
        private val lastPlayedTitle = stringPreferencesKey("lastPlayedTitle")
        private val lastPlayedScore = stringPreferencesKey("lastPlayedScore")

        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    override val sizeMode = SizeMode.Responsive(
        setOf(
            SMALL_SQUARE,
            HORIZONTAL_RECTANGLE,
            BIG_SQUARE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val scope = rememberCoroutineScope()
            val size = LocalSize.current

            LaunchedEffect(key1 = CFQUser.mode) {
                scope.launch {
                    println("App Widget Updated.")
                    when (CFQUser.mode) {
                        0 -> switchToChunithm(context, id)
                        1 -> switchToMaimai(context, id)
                    }
                }
            }

            GlanceTheme {
                if (size.width >= HORIZONTAL_RECTANGLE.width) {
                    WidgetContent()
                }
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<Preferences>()

        val nickname = prefs[nickname] ?: CFQUser.username
        val rating = prefs[rating] ?: ""
        val playCount = prefs[playCount] ?: ""
        val lastUpdate = prefs[lastUpdate] ?: ""

        val lastPlayedImage = prefs[lastPlayedImage] ?: ""
        val lastPlayedTitle = prefs[lastPlayedTitle] ?: ""
        val lastPlayedScore = prefs[lastPlayedScore] ?: ""

        Column (
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
        ) {
            Text(text = nickname, style = TextStyle(fontWeight = FontWeight.Bold))
            Row {
                Column {
                    Text(text = rating, style = TextStyle(fontWeight = FontWeight.Bold))
                    Text(text = "Rating")
                }

                Column {
                    Text(text = playCount, style = TextStyle(fontWeight = FontWeight.Bold))
                    Text(text = "游玩次数")
                }

                Column {
                    Text(text = lastUpdate, style = TextStyle(fontWeight = FontWeight.Bold))
                    Text(text = "最近更新")
                }
            }
        }
    }

    private suspend fun switchToChunithm(context: Context, id: GlanceId) {
        updateAppWidgetState(context, id) {
            it[nickname] = CFQUser.chunithm.info.nickname
            it[rating] = String.format(Locale.getDefault(),"%.2f", CFQUser.chunithm.info.rating)
            it[playCount] = CFQUser.chunithm.info.playCount.toString()
            it[lastUpdate] = CFQUser.chunithm.aux.updateTime
        }
    }

    private suspend fun switchToMaimai(context: Context, id: GlanceId) {
        updateAppWidgetState(context, id) {
            it[nickname] = CFQUser.maimai.info.nickname
            it[rating] = CFQUser.maimai.info.rating.toString()
            it[playCount] = CFQUser.maimai.info.playCount.toString()
            it[lastUpdate] = CFQUser.maimai.aux.updateTime
        }
    }
}