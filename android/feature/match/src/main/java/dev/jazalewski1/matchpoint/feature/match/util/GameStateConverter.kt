package dev.jazalewski1.matchpoint.feature.match.util

import dev.jazalewski1.matchpoint.domain.tennis.GameState

internal fun GameState.lhsToString() =
    when (this) {
        is GameState.Regular.Main -> this.lhs.value.toString()
        is GameState.Regular.Deuce -> "40"
        is GameState.Regular.Advantage.Lhs -> "AD"
        is GameState.Regular.Advantage.Rhs -> "40"
        is GameState.TieBreak -> this.lhs.toString()
    }

internal fun GameState.rhsToString() =
    when (this) {
        is GameState.Regular.Main -> this.rhs.value.toString()
        is GameState.Regular.Deuce -> "40"
        is GameState.Regular.Advantage.Lhs -> "40"
        is GameState.Regular.Advantage.Rhs -> "AD"
        is GameState.TieBreak -> this.rhs.toString()
    }
