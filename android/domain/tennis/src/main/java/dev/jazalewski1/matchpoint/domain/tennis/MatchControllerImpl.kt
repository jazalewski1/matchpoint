package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.domain.tennis.controllers.*
import dev.jazalewski1.matchpoint.domain.tennis.controllers.Set
import dev.jazalewski1.matchpoint.domain.tennis.details.*

private const val NUM_OF_SETS_TO_WIN = 3 // TODO: Temporary until setting is implemented

class MatchControllerImpl : MatchController {
    private var game: Game = RegularGame()
    private var set = Set()
    private val match = Match(numOfSetsToWin = NUM_OF_SETS_TO_WIN)

    override fun getState(): TotalMatchState {
        return TotalMatchState(
            game = game.toState(),
            set = set.toState(),
            match = match.toState(),
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
            is SetOutcome.Tiebreak -> {
                game = TieBreakGame()
                return MatchEvent.GameWon
            }
            is SetOutcome.Finished -> return processSetFinished(winner = winner)
        }
    }

    private fun processSetFinished(winner: Player): MatchEvent {
        when (match.addSet(winner)) {
            is MatchOutcome.None -> {
                game = RegularGame()
                set = Set()
                return MatchEvent.SetWon
            }
            is MatchOutcome.Finished -> return MatchEvent.MatchWon
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

private fun Match.toState() = MatchState(lhsSets = player1Sets, rhsSets = player2Sets)
