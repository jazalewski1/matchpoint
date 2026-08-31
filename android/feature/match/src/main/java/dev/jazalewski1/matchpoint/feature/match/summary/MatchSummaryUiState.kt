package dev.jazalewski1.matchpoint.feature.match.summary

sealed interface MatchSummaryUiState {
    data class Loaded(
        val player1: PlayerUiState,
        val player2: PlayerUiState,
        val numOfSets: Int,
    ) : MatchSummaryUiState

    data class Error(val message: String) : MatchSummaryUiState
}

data class PlayerUiState(
    val name: String,
    val sets: List<SetUiState>,
)

data class SetUiState(
    val games: Int,
    val isWinner: Boolean,
    val tieBreakPoints: Int? = null,
)
