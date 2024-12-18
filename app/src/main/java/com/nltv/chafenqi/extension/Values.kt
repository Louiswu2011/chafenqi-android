package com.nltv.chafenqi.extension

import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import kotlin.random.nextInt

// 23 in total
var MAIMAI_LEVEL_STRINGS = listOf(
    "1",
    "2",
    "3",
    "4",
    "5",
    "5+",
    "6",
    "6+",
    "7",
    "7+",
    "8",
    "8+",
    "9",
    "9+",
    "10",
    "10+",
    "11",
    "11+",
    "12",
    "12+",
    "13",
    "13+",
    "14",
    "14+",
    "15"
)
var CHUNITHM_LEVEL_STRINGS = listOf(
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    "6+",
    "7",
    "7+",
    "8",
    "8+",
    "9",
    "9+",
    "10",
    "10+",
    "11",
    "11+",
    "12",
    "12+",
    "13",
    "13+",
    "14",
    "14+",
    "15"
)
val RATE_STRINGS_MAIMAI = listOf("其他", "AAA", "S", "S+", "SS", "SS+", "SSS", "SSS+").reversed()
val RATE_STRINGS_CHUNITHM = listOf("其他", "S", "S+", "SS", "SS+", "SSS", "SSS+").reversed()
val RATE_COLORS_MAIMAI = listOf(
    Color(0xff444444),
    Color(0xff785d83),
    Color(0xff8e0a01),
    Color(0xfffe2f20),
    Color(0xff369656),
    Color(0xff78ce95),
    Color(0xffca8402),
    Color(0xFFFFA800)
).reversed()
val RATE_COLORS_CHUNITHM = listOf(
    Color(0xff444444),
    Color(0xff8e0a01),
    Color(0xfffe2f20),
    Color(0xff369656),
    Color(0xff78ce95),
    Color(0xffca8402),
    Color(0xFFFFA800)
).reversed()
val STATUS_STRINGS =
    listOf("Failed", "Clear", "Full Combo", "Full Combo+", "All Perfect", "All Perfect+")
var MAIMAI_VERSION_STRINGS = mapOf<Int, String>()
var MAIMAI_GENRE_STRINGS = listOf<String>()
var CHUNITHM_VERSION_STRINGS = listOf<String>()
var CHUNITHM_GENRE_STRINGS = listOf<String>()

val MAIMAI_NOTE_TYPE = listOf(
    "Tap", "Hold", "Slide", "Touch"
)
val MAIMAI_MISS_JUDGE_TYPE = listOf(
    "Great", "Good", "Miss"
)

const val TEAM_CODE_LENGTH = 8
const val TEAM_NAME_LENGTH = 24
const val TEAM_REMARKS_LENGTH = 128

fun randomColor(): Color = Color(
    red = Random.nextInt(0..255),
    blue = Random.nextInt(0..255),
    green = Random.nextInt(0..255)
)