package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.core.common.Side

sealed interface GameState {
    sealed interface Regular : GameState {
        data class Main(val lhsPoints: Points, val rhsPoints: Points) : Regular

        object Deuce : Regular

        data class Advantage(val side: Side) : Regular
    }

    data class TieBreak(val lhsPoints: Int, val rhsPoints: Int) : GameState

    companion object {
        fun default() = Regular.Main(lhsPoints = Points.LOVE, rhsPoints = Points.LOVE)
    }
}
