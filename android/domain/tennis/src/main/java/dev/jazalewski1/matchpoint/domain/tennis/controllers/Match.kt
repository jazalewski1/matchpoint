package dev.jazalewski1.matchpoint.domain.tennis.controllers

import dev.jazalewski1.matchpoint.domain.tennis.details.Player

internal sealed interface MatchOutcome {
    data object None : MatchOutcome

    data class Finished(val winner: Player) : MatchOutcome
}

internal class Match(private val numOfSetsToWin: Int) {
    var player1Sets = 0
        private set

    var player2Sets = 0
        private set

    fun addSet(winner: Player): MatchOutcome {
        when (winner) {
            Player.ONE -> ++player1Sets
            Player.TWO -> ++player2Sets
        }
        if (player1Sets >= numOfSetsToWin) {
            return MatchOutcome.Finished(winner = Player.ONE)
        }
        if (player2Sets >= numOfSetsToWin) {
            return MatchOutcome.Finished(winner = Player.TWO)
        }
        return MatchOutcome.None
    }
}
