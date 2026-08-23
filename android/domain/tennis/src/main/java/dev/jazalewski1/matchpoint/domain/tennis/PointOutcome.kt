package dev.jazalewski1.matchpoint.domain.tennis

sealed interface PointOutcome {
    data object PointScored : PointOutcome

    data object GameWon : PointOutcome
}
