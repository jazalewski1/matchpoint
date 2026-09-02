package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.domain.tennis.controllers.*
import dev.jazalewski1.matchpoint.domain.tennis.controllers.Set

class MatchControllerImpl(private val numOfSetsToWin: Int) : MatchController {
    private var game: Game = RegularGame()
    private var set = Set()
    private val match = Match(numOfSetsToWin = numOfSetsToWin)
    private val setHistory = mutableListOf<MatchHistory.Set>()
    private val sideConfig = SideConfig()

    override fun getState(): TotalMatchState { // TODO: rename to getCurrentGame
        return TotalMatchState(game = game.toState())
    }

    override fun addPointToLhs(): MatchEvent = processPointScored(Player.ONE)

    override fun addPointToRhs(): MatchEvent = processPointScored(Player.TWO)

    override fun getHistory() = MatchHistory(sets = setHistory)

    private fun processPointScored(winner: Player): MatchEvent =
        when (game.addPoint(winner)) {
            is GameOutcome.PointScored ->
                MatchEvent.PointScored(winnerSide = sideConfig.getSide(winner))
            is GameOutcome.Finished -> processGameFinished(winner = winner)
        }

    private fun processGameFinished(winner: Player): MatchEvent {
        when (set.addGame(winner)) {
            is SetOutcome.None -> {
                game = RegularGame()
                return MatchEvent.GameWon(
                    winnerSide = sideConfig.getSide(winner),
                    lhsGames = sideConfig.selectLhs(p1 = set.player1Games, p2 = set.player2Games),
                    rhsGames = sideConfig.selectRhs(p1 = set.player1Games, p2 = set.player2Games),
                )
            }
            is SetOutcome.Tiebreak -> {
                game = TieBreakGame()
                return MatchEvent.GameWon(
                    winnerSide = sideConfig.getSide(winner),
                    lhsGames = sideConfig.selectLhs(p1 = set.player1Games, p2 = set.player2Games),
                    rhsGames = sideConfig.selectRhs(p1 = set.player1Games, p2 = set.player2Games),
                )
            }
            is SetOutcome.Finished -> return processSetFinished(winner = winner)
        }
    }

    private fun processSetFinished(winner: Player): MatchEvent {
        saveSetToHistory(winner)
        when (match.addSet(winner)) {
            is MatchOutcome.None -> {
                game = RegularGame()
                set = Set()
                return MatchEvent.SetWon(
                    winnerSide = sideConfig.getSide(winner),
                    lhsSets = sideConfig.selectLhs(p1 = match.player1Sets, p2 = match.player2Sets),
                    rhsSets = sideConfig.selectRhs(p1 = match.player1Sets, p2 = match.player2Sets),
                )
            }
            is MatchOutcome.Finished ->
                return MatchEvent.MatchWon(
                    winnerSide = sideConfig.getSide(winner),
                    lhsSets = sideConfig.selectLhs(p1 = match.player1Sets, p2 = match.player2Sets),
                    rhsSets = sideConfig.selectRhs(p1 = match.player1Sets, p2 = match.player2Sets),
                )
        }
    }

    private fun saveSetToHistory(winner: Player) {
        val storedTb =
            (game as? TieBreakGame)?.let { tieBreakGame ->
                MatchHistory.Set.TieBreak(
                    player1Points = tieBreakGame.player1,
                    player2Points = tieBreakGame.player2,
                )
            }
        val storedSet =
            MatchHistory.Set(
                player1Games = set.player1Games,
                player2Games = set.player2Games,
                winner = winner,
                tieBreak = storedTb,
            )
        setHistory.add(storedSet)
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

private class SideConfig {
    var playerOnLhs = Player.ONE
        private set

    fun getSide(player: Player) =
        if (playerOnLhs == player) {
            Side.LHS
        } else {
            Side.RHS
        }

    fun <T> selectLhs(p1: T, p2: T) = if (playerOnLhs == Player.ONE) p1 else p2

    fun <T> selectRhs(p1: T, p2: T) = if (playerOnLhs == Player.ONE) p2 else p1
}
