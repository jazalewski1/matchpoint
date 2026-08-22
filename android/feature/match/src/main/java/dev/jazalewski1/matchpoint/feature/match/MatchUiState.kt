package dev.jazalewski1.matchpoint.feature.match

data class MatchUiState(
    val lhsPlayer: PlayerUiState,
    val rhsPlayer: PlayerUiState,
)

sealed interface Indication {
    data object Minor : Indication

    data object Major : Indication
}

data class PlayerUiState(
    val name: String,
    val score: String,
    val indication: Indication? = null,
)
