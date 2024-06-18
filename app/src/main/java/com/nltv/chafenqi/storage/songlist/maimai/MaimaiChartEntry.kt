package com.nltv.chafenqi.storage.songlist.maimai

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class MaimaiChartEntry(
    val notes: List<Int> = listOf(),
    val charter: String = ""
) {
    private fun appendNotation(score: Double) = "-${String.format(Locale.ENGLISH, "%.4f", score)}%"

    val type = if (notes.size == 4) 0 else 1
    private val errors: List<Double>
        get() {
            val basicUnitScore = if (type == 0) {
                100.0 / (notes[0] + 2 * notes[1] + 3 * notes[2] + 5 * notes[3])
            } else {
                100.0 / (notes[0] + 2 * notes[1] + 3 * notes[2] + notes[3] + 5 * notes[4])
            }
            val breakUnitScore = 1.0 / notes.last()
            return listOf(basicUnitScore, breakUnitScore)
        }

    val possibleNormalLoss: List<List<String>>
        get() {
            val unitScores = this.errors
            val nor = unitScores[0]
            return listOf(
                listOf(appendNotation(nor * 0.2), appendNotation(nor * 0.5), appendNotation(nor)),
                listOf(appendNotation(nor * 0.4), appendNotation(nor), appendNotation(nor * 2)),
                listOf(
                    appendNotation(nor * 0.6),
                    appendNotation(nor * 1.5),
                    appendNotation(nor * 3)
                ),
                if (type == 1) listOf(
                    appendNotation(nor * 0.2),
                    appendNotation(nor * 0.5),
                    appendNotation(nor)
                ) else emptyList()
            )
        }

    val possibleBreakLoss: List<String>
        get() {
            val unitScores = this.errors
            val nor = unitScores[0]
            val br = unitScores[1]
            return listOf(
                appendNotation(br * 0.25),
                appendNotation(br * 0.5),
                appendNotation(br * 0.6 + nor),
                appendNotation(br * 0.6 + nor * 2),
                appendNotation(br * 0.6 + nor * 2.5),
                appendNotation(br * 0.7 + nor * 3),
                appendNotation(br + nor * 5)
            )
        }

    val lossUntilSSS = 1.0 / (this.errors[0] * 0.2)
    val lossUntilSSSPlus = 0.5 / (this.errors[0] * 0.2)
    val breakToGreatRatio = (this.errors[1] * 0.25) / (this.errors[0] * 0.2)
}