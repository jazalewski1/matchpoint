package dev.jazalewski1.matchpoint.domain.tennis.controllers

import dev.jazalewski1.matchpoint.domain.tennis.Player
import dev.jazalewski1.matchpoint.domain.tennis.Points

internal sealed interface GameOutcome {
    data class PointScored(val winner: Player) : GameOutcome

    data class Finished(val winner: Player) : GameOutcome
}

internal sealed interface Game {
    fun addPoint(winner: Player): GameOutcome
}

internal sealed interface Phase {
    fun next(pointWinner: Player): Phase?

    data class Main(val player1: Points, val player2: Points) : Phase {
        override fun next(pointWinner: Player): Phase? =
            when (pointWinner) {
                Player.ONE -> {
                    val nextPlayer1 = player1.next() ?: return null
                    if (nextPlayer1 == Points.FORTY && player2 == Points.FORTY) {
                        return Deuce
                    }
                    return this.copy(player1 = nextPlayer1)
                }
                Player.TWO -> {
                    val nextPlayer2 = player2.next() ?: return null
                    if (nextPlayer2 == Points.FORTY && player1 == Points.FORTY) {
                        return Deuce
                    }
                    return this.copy(player2 = nextPlayer2)
                }
            }
    }

    data object Deuce : Phase {
        override fun next(pointWinner: Player) = Advantage(player = pointWinner)
    }

    data class Advantage(val player: Player) : Phase {
        override fun next(pointWinner: Player): Phase? {
            if (pointWinner == player) {
                return null
            }
            return Deuce
        }
    }
}

internal class RegularGame : Game {
    var phase: Phase = Phase.Main(Points.LOVE, Points.LOVE)
        private set

    override fun addPoint(winner: Player): GameOutcome {
        val nextPhase =
            phase.next(pointWinner = winner) ?: return GameOutcome.Finished(winner = winner)
        phase = nextPhase
        return GameOutcome.PointScored(winner = winner)
    }
}

internal class TieBreakGame : Game {
    var player1 = 0
        private set

    var player2 = 0
        private set

    override fun addPoint(winner: Player): GameOutcome {
        when (winner) {
            Player.ONE -> player1 += 1
            Player.TWO -> player2 += 1
        }
        if (player1 >= 7 && player1 >= (player2 + 2)) {
            return GameOutcome.Finished(winner = Player.ONE)
        }
        if (player2 >= 7 && player2 >= (player1 + 2)) {
            return GameOutcome.Finished(winner = Player.TWO)
        }
        return GameOutcome.PointScored(winner = winner)
    }
}
