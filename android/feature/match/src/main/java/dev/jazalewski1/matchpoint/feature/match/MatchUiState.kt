package dev.jazalewski1.matchpoint.feature.match

data class PlayerUiState(
    val name: String,
    val score: String,
)

data class GameUiState(
    val lhsPlayer: PlayerUiState,
    val rhsPlayer: PlayerUiState,
    val isTieBreak: Boolean,
)

data class MatchUiState(val game: GameUiState)
