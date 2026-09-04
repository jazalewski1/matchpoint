package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.core.common.Side

sealed interface MatchEvent {
    data class PointScored(val winnerSide: Side) : MatchEvent

    data class GameFinished(
        val winnerSide: Side,
        val lhsGames: Int,
        val rhsGames: Int,
    ) : MatchEvent

    data class SetFinished(
        val winnerSide: Side,
        val lhsSets: Int,
        val rhsSets: Int,
    ) : MatchEvent

    data class MatchFinished(
        val winnerSide: Side,
        val lhsSets: Int,
        val rhsSets: Int,
    ) : MatchEvent
}
