package com.nltv.chafenqi.storage.songlist.maimai

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class MaimaiChartEntry(
    val notes: List<Int> = listOf(),
    val charter: String = ""
) {
    private fun appendNotation(score: Double) = "-${String.format(Locale.ENGLISH, "%.4f", score)}%"

    val type = if (notes.size == 5) MaimaiChartType.Deluxe else MaimaiChartType.Standard
    private val basicUnitScore = if (type == MaimaiChartType.Standard) {
        100.0 / (notes[0] + 2 * notes[1] + 3 * notes[2] + 5 * notes[3])
    } else {
        100.0 / (notes[0] + 2 * notes[1] + 3 * notes[2] + notes[3] + 5 * notes[4])
    }

    private val breakUnitScore = 1.0 / notes.last()

    val possibleNormalLoss = listOf(
        listOf(
            appendNotation(basicUnitScore * 0.2),
            appendNotation(basicUnitScore * 0.5),
            appendNotation(basicUnitScore)
        ),
        listOf(
            appendNotation(basicUnitScore * 0.4),
            appendNotation(basicUnitScore),
            appendNotation(basicUnitScore * 2)
        ),
        listOf(
            appendNotation(basicUnitScore * 0.6),
            appendNotation(basicUnitScore * 1.5),
            appendNotation(basicUnitScore * 3)
        ),
        if (type == MaimaiChartType.Deluxe) listOf(
            appendNotation(basicUnitScore * 0.2),
            appendNotation(basicUnitScore * 0.5),
            appendNotation(basicUnitScore)
        ) else emptyList()
    )

    val possibleBreakLoss = listOf(
        appendNotation(breakUnitScore * 0.25),
        appendNotation(breakUnitScore * 0.5),
        appendNotation(breakUnitScore * 0.6 + basicUnitScore),
        appendNotation(breakUnitScore * 0.6 + basicUnitScore * 2),
        appendNotation(breakUnitScore * 0.6 + basicUnitScore * 2.5),
        appendNotation(breakUnitScore * 0.7 + basicUnitScore * 3),
        appendNotation(breakUnitScore + basicUnitScore * 5)
    )

    val lossUntilSSS = 1.0 / (basicUnitScore * 0.2)
    val lossUntilSSSPlus = 0.5 / (basicUnitScore * 0.2)
    val breakToGreatRatio = (breakUnitScore * 0.25) / (basicUnitScore * 0.2)
}

enum class MaimaiChartType {
    Standard, Deluxe, Utage
}