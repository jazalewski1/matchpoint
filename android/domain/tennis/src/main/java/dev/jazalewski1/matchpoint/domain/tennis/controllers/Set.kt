package dev.jazalewski1.matchpoint.domain.tennis.controllers

import dev.jazalewski1.matchpoint.domain.tennis.details.Player

internal sealed interface SetOutcome {
    data object None : SetOutcome

    data class Finished(val winner: Player) : SetOutcome

    data object Tiebreak : SetOutcome
}

internal class Set {
    var player1Games = 0
        private set

    var player2Games = 0
        private set

    fun addGame(winner: Player): SetOutcome {
        when (winner) {
            Player.ONE -> player1Games += 1
            Player.TWO -> player2Games += 1
        }
        return evaluate()
    }

    private fun evaluate(): SetOutcome {
        if (player1Games == 6 && player2Games <= 4) {
            return SetOutcome.Finished(winner = Player.ONE)
        }
        if (player2Games == 6 && player1Games <= 4) {
            return SetOutcome.Finished(winner = Player.TWO)
        }
        if (player1Games == 7 && player2Games <= 5) {
            return SetOutcome.Finished(winner = Player.ONE)
        }
        if (player2Games == 7 && player1Games <= 5) {
            return SetOutcome.Finished(winner = Player.TWO)
        }
        if (player1Games == 6 && player2Games == 6) {
            return SetOutcome.Tiebreak
        }
        if (player1Games == 7 && player2Games <= 6) {
            return SetOutcome.Finished(winner = Player.ONE)
        }
        if (player2Games == 7 && player1Games <= 6) {
            return SetOutcome.Finished(winner = Player.TWO)
        }
        return SetOutcome.None
    }
}
