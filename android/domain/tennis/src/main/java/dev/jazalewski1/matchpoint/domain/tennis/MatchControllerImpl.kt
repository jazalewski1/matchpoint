package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.domain.tennis.controllers.*
import dev.jazalewski1.matchpoint.domain.tennis.controllers.Set
import dev.jazalewski1.matchpoint.domain.tennis.details.*

class MatchControllerImpl : MatchController {
    private var game: Game = RegularGame()
    private var set = Set()
    // To be handled by Match class
    private var player1Sets = 0
    private var player2Sets = 0

    override fun getState(): MatchState {
        return MatchState(
            game = game.toState(),
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

private fun Game.toState() =
    when (this) {
        is RegularGame -> {
            when (val current = phase) {
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
                lhsPoints = player1,
                rhsPoints = player2,
            )
        }
    }

private fun Set.toState() = SetState(lhsGames = player1Games, rhsGames = player2Games)
