package dev.jazalewski1.matchpoint.feature.match.util

import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.domain.tennis.GameState

internal fun GameState.lhsToString() =
    when (this) {
        is GameState.Regular.Main -> this.lhsPoints.value.toString()
        is GameState.Regular.Deuce -> "40"
        is GameState.Regular.Advantage -> if (side == Side.LHS) "AD" else "40"
        is GameState.TieBreak -> this.lhsPoints.toString()
    }

internal fun GameState.rhsToString() =
    when (this) {
        is GameState.Regular.Main -> this.rhsPoints.value.toString()
        is GameState.Regular.Deuce -> "40"
        is GameState.Regular.Advantage -> if (side == Side.RHS) "AD" else "40"
        is GameState.TieBreak -> this.rhsPoints.toString()
    }
