package dev.jazalewski1.matchpoint.domain.tennis

sealed interface PointOutcome {
    data class PointScored(val side: Side) : PointOutcome

    data class GameWon(val side: Side) : PointOutcome
}

enum class Side {
    LHS,
    RHS,
}
