package dev.jazalewski1.matchpoint.feature.match.main

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

    data class MatchFinished(
        val winner: Side,
        val lhsScore: String,
        val rhsScore: String,
    ) : MatchUiEvent
}

sealed interface MatchNavigationEvent {
    data class MatchFinished(val matchId: Long) : MatchNavigationEvent
}
