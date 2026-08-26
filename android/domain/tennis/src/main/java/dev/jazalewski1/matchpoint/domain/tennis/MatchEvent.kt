package dev.jazalewski1.matchpoint.domain.tennis

sealed interface MatchEvent {
    data object PointScored : MatchEvent

    data object GameWon : MatchEvent

    data object SetWon : MatchEvent
}
