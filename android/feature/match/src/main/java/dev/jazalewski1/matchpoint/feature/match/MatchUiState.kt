package dev.jazalewski1.matchpoint.feature.match

data class PlayerUiState(
    val name: String,
    val score: String,
)

enum class Side {
    LHS, RHS;
}

sealed interface IndicationType{
    data object Minor : IndicationType

    data object Major : IndicationType
}

data class Indication(
    val key: Int,
    val type: IndicationType,
    val side: Side,
)

data class MatchUiState(
    val lhsPlayer: PlayerUiState,
    val rhsPlayer: PlayerUiState,
    val indication: Indication? = null,
)
