package dev.jazalewski1.matchpoint.feature.match.summary

internal sealed interface UiState

data class LoadedUiState(
    val player1: Player,
    val player2: Player,
    val numOfSets: Int,
) : UiState {
    data class Player(
        val name: String,
        val sets: List<Set>,
    )

    data class Set(
        val games: Int,
        val isWinner: Boolean,
        val tieBreakPoints: Int? = null,
    )
}

internal data class ErrorUiState(val message: String) : UiState
