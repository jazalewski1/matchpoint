package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.core.common.Side

// TODO: rename *Won to *Finished
sealed interface MatchEvent {
    data class PointScored(val winnerSide: Side) : MatchEvent

    data class GameWon(
        val winnerSide: Side,
        val lhsGames: Int,
        val rhsGames: Int,
    ) : MatchEvent

    data class SetWon(
        val winnerSide: Side,
        val lhsSets: Int,
        val rhsSets: Int,
    ) : MatchEvent

    data class MatchWon(
        val winnerSide: Side,
        val lhsSets: Int,
        val rhsSets: Int,
    ) : MatchEvent
}
