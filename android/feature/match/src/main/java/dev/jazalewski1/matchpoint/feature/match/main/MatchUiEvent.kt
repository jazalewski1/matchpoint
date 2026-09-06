package dev.jazalewski1.matchpoint.feature.match.main

import dev.jazalewski1.matchpoint.core.common.Side

internal sealed interface MatchUiEvent {
    data class PointScored(val winner: Side, val withSideSwitch: Boolean) : MatchUiEvent

    data class GameFinished(
        val winner: Side,
        val lhsScore: String,
        val rhsScore: String,
        val withSideSwitch: Boolean,
    ) : MatchUiEvent

    data class SetFinished(
        val winner: Side,
        val lhsScore: String,
        val rhsScore: String,
        val withSideSwitch: Boolean,
    ) : MatchUiEvent

    data class MatchFinished(
        val winner: Side,
        val lhsScore: String,
        val rhsScore: String,
    ) : MatchUiEvent
}

internal sealed interface MatchNavigationEvent {
    data class Finish(val matchId: Long) : MatchNavigationEvent
}
