package com.nltv.chafenqi.util

import androidx.compose.ui.graphics.Color
import com.nltv.chafenqi.view.home.nameplateChunithmBottomColor
import com.nltv.chafenqi.view.home.nameplateChunithmTopColor
import com.nltv.chafenqi.view.home.nameplateMaimaiBottomColor
import com.nltv.chafenqi.view.home.nameplateMaimaiTopColor

fun getGradientStop(from: Color, to: Color, ratio: Float): Color {
    val red = from.red + (to.red - from.red) * ratio
    val green = from.green + (to.green - from.green) * ratio
    val blue = from.blue + (to.blue - from.blue) * ratio
    val alpha = from.alpha + (to.alpha - from.alpha) * ratio
    return Color(red, green, blue, alpha)
}

fun getMaimaiCardGradientStop(ratio: Float): Color =
    getGradientStop(nameplateMaimaiTopColor, nameplateMaimaiBottomColor, ratio)

fun getChunithmCardGradientStop(ratio: Float): Color =
    getGradientStop(nameplateChunithmTopColor, nameplateChunithmBottomColor, ratio)