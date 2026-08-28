package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.domain.tennis.controllers.*
import dev.jazalewski1.matchpoint.domain.tennis.details.*

class MatchControllerImpl : MatchController {
    private var game: Game = RegularGame()
    private var set = Set()
    // To be handled by Match class
    private var player1Sets = 0
    private var player2Sets = 0

    override fun getState(): MatchState {
        val gameState =
            when (val kind = game) {
                is RegularGame -> {
                    when (val current = kind.phase) {
                        is Phase.Main ->
                            GameState.Regular.Main(
                                lhsPoints = current.player1,
                                rhsPoints = current.player2,
                            )

                        is Phase.Deuce -> GameState.Regular.Deuce
                        is Phase.Advantage ->
                            when (current.player) {
                                Player.ONE -> GameState.Regular.Advantage.Lhs
                                Player.TWO -> GameState.Regular.Advantage.Rhs
                            }
                    }
                }
                is TieBreakGame -> {
                    GameState.TieBreak(
                        lhsPoints = kind.player1,
                        rhsPoints = kind.player2,
                    )
                }
            }
        return MatchState(
            game = gameState,
            set = set.toState(),
            lhsSets = player1Sets,
            rhsSets = player2Sets,
        )
    }

    override fun addPointToLhs(): MatchEvent = processPointScored(Player.ONE)

    override fun addPointToRhs(): MatchEvent = processPointScored(Player.TWO)

    private fun processPointScored(winner: Player): MatchEvent =
        when (game.addPoint(winner)) {
            is GameOutcome.PointScored -> MatchEvent.PointScored
            is GameOutcome.Finished -> processGameFinished(winner = winner)
        }

    private fun processGameFinished(winner: Player): MatchEvent {
        if (game is TieBreakGame) {
            game = RegularGame()
            set = Set()
            when (winner) {
                Player.ONE -> player1Sets += 1
                Player.TWO -> player2Sets += 1
            }
            return MatchEvent.SetWon
        }
        when (set.addGame(winner)) {
            is SetOutcome.None -> {
                game = RegularGame()
                return MatchEvent.GameWon
            }
            is SetOutcome.Finished -> {
                game = RegularGame()
                set = Set()
                when (winner) {
                    Player.ONE -> player1Sets += 1
                    Player.TWO -> player2Sets += 1
                }
                return MatchEvent.SetWon
            }
            is SetOutcome.Tiebreak -> {
                game = TieBreakGame()
                return MatchEvent.GameWon
            }
        }
    }
}

private sealed interface SetOutcome {
    data object None : SetOutcome

    data class Finished(val winner: Player) : SetOutcome

    data object Tiebreak : SetOutcome
}

private class Set {
    private var player1Games = 0
    private var player2Games = 0

    fun addGame(winner: Player): SetOutcome {
        when (winner) {
            Player.ONE -> player1Games += 1
            Player.TWO -> player2Games += 1
        }
        return evaluate()
    }

    // TODO: Should be done by MC when changing sides is implemented
    fun toState() = SetState(lhsGames = player1Games, rhsGames = player2Games)

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
        return SetOutcome.None
    }
}
