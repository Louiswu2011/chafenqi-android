package com.nltv.chafenqi.view.module

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nltv.chafenqi.SCREEN_PADDING

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

@Composable
fun RatingBadge() {
    Column (
        Modifier.width(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SSS+",
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFFFE57F))
                .border(
                    border = BorderStroke(2.dp, RAINBOW_BRUSH),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 5.dp, vertical = 2.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBadgePreview() {
    RatingBadge()
}