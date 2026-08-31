package dev.jazalewski1.matchpoint.core.data

data class MatchDetails(
    val player1Name: String,
    val player2Name: String,
    val sets: List<Set>,
) {
    data class Set(
        val player1Games: Int,
        val player2Games: Int,
        val winner: Player,
        val tieBreak: TieBreak? = null,
    ) {
        data class TieBreak(val player1Points: Int, val player2Points: Int)
    }
}

enum class Player {
    ONE,
    TWO,
}
