package dev.jazalewski1.matchpoint.feature.match

enum class Side {
    LHS,
    RHS,
}

sealed interface MatchUiEvent {
    data class PointScored(val winner: Side) : MatchUiEvent

    data class GameFinished(
        val winner: Side,
        val lhsScore: String,
        val rhsScore: String,
    ) : MatchUiEvent

    data class SetFinished(
        val winner: Side,
        val lhsScore: String,
        val rhsScore: String,
    ) : MatchUiEvent
}
