package dev.jazalewski1.matchpoint.feature.match.util

import dev.jazalewski1.matchpoint.domain.tennis.GameState

internal fun GameState.lhsToString() =
    when (this) {
        is GameState.Regular -> this.lhs.value.toString()
        is GameState.Deuce -> "40"
        is GameState.Advantage.Lhs -> "AD"
        is GameState.Advantage.Rhs -> "40"
    }

internal fun GameState.rhsToString() =
    when (this) {
        is GameState.Regular -> this.rhs.value.toString()
        is GameState.Deuce -> "40"
        is GameState.Advantage.Lhs -> "40"
        is GameState.Advantage.Rhs -> "AD"
    }
