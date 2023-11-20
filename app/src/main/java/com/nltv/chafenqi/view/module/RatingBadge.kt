package com.nltv.chafenqi.view.module

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nltv.chafenqi.extension.conditional

val RAINBOW_BRUSH = Brush.linearGradient(
    listOf(
        Color(0xFF9575CD),
        Color(0xFFBA68C8),
        Color(0xFFE57373),
        Color(0xFFFFB74D),
        Color(0xFFFFF176),
        Color(0xFFAED581),
        Color(0xFF4DD0E1),
        Color(0xFF9575CD)
    )
)

val HIGH_BADGE_BACKGROUND_COLOR = Color(red = 255, green = 209, blue = 128, alpha = 255)
val LOW_BADGE_BACKGROUND_COLOR = Color(red = 191, green = 155, blue = 48)
val DEFAULT_BADGE_BACKGROUND_COLOR = Color.Gray

@Composable
fun RatingBadge(rate: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.width(45.dp)
    ) {
        Text(
            text = rate,
            modifier = Modifier
                .width(45.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(
                    when (rate) {
                        "SSS+", "SSS", "SS+", "SS", "S+", "S" -> HIGH_BADGE_BACKGROUND_COLOR
                        "AAA" -> LOW_BADGE_BACKGROUND_COLOR
                        else -> DEFAULT_BADGE_BACKGROUND_COLOR
                    }
                )
                .conditional(rate == "SSS+") {
                    Modifier.border(
                        border = BorderStroke(2.dp, RAINBOW_BRUSH),
                        shape = RoundedCornerShape(5.dp)
                    )
                }
                .padding(horizontal = 5.dp, vertical = 2.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = when (rate) {
                "SSS+", "SSS", "SS+", "SS", "S+", "S", "AAA" -> Color.Black
                else -> Color.White
            },
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBadgePreview() {
    RatingBadge("SSS+")
}