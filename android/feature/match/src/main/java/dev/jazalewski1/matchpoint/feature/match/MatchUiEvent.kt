package dev.jazalewski1.matchpoint.feature.match

enum class Side {
    LHS,
    RHS,
}

sealed interface MatchUiEvent {
    data class PointScored(val winner: Side) : MatchUiEvent

    data class GameFinished(val winner: Side) : MatchUiEvent
}
