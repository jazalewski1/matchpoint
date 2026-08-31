package dev.jazalewski1.matchpoint.feature.match.main

data class UiState(val game: Game) {
    data class Game(
        val lhsPlayer: Player,
        val rhsPlayer: Player,
        val isTieBreak: Boolean,
    )

    data class Player(
        val name: String,
        val score: String,
    )
}
