package dev.jazalewski1.matchpoint.feature.match

data class PlayerUiState(
    val name: String,
    val score: String,
)

data class MatchUiState(
    val lhsPlayer: PlayerUiState,
    val rhsPlayer: PlayerUiState,
)
