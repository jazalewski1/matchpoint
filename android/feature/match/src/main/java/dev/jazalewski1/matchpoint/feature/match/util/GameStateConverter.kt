package dev.jazalewski1.matchpoint.feature.match.util

import dev.jazalewski1.matchpoint.domain.tennis.GameState

internal fun GameState.toPairOfStrings() =
    when (this) {
        is GameState.Ongoing -> Pair(this.lhs.value.toString(), this.rhs.value.toString())
        is GameState.Deuce -> Pair("40", "40")
        is GameState.Advantage.Lhs -> Pair("AD", "40")
        is GameState.Advantage.Rhs -> Pair("40", "AD")
    }
