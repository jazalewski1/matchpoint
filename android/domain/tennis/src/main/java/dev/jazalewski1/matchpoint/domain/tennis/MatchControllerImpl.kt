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
    private var sideConfig = SideConfig()
    private var isOngoing = true

    override fun getCurrentGameState() = game.toState(sideConfig)

    override fun getSideConfig() = sideConfig

    override fun addPointToLhs(): MatchEvent? = addPoint(Side.LHS)

    override fun addPointToRhs(): MatchEvent? = addPoint(Side.RHS)

    override fun getHistory() = MatchHistory(sets = setHistory)

    private fun addPoint(side: Side): MatchEvent? {
        if (!isOngoing) {
            return null
        }
        val player = sideConfig.getPlayer(side)
        val outcome = evaluatePointScored(winner = player)
        val event = toMatchEvent(outcome)
        sideConfig = nextSideConfig(outcome)
        performTransition(outcome)
        return event
    }

    private fun evaluatePointScored(winner: Player) =
        when (game.addPoint(winner)) {
            is GameOutcome.PointScored -> Outcome.PointScored(winner = winner, withSideSwitch = shouldSwitchSides(game))
            is GameOutcome.Finished -> evaluateGameFinished(winner = winner)
        }

    private fun evaluateGameFinished(winner: Player) =
        when (set.addGame(winner)) {
            is SetOutcome.None -> Outcome.GameFinished(winner, toTieBreak = false, withSideSwitch = shouldSwitchSides(set))
            is SetOutcome.Tiebreak -> Outcome.GameFinished(winner, toTieBreak = true, withSideSwitch = shouldSwitchSides(set))
            is SetOutcome.Finished -> evaluateSetFinished(winner = winner)
        }

    private fun evaluateSetFinished(winner: Player) =
        when (match.addSet(winner)) {
            is MatchOutcome.None -> Outcome.SetFinished(winner, withSideSwitch = shouldSwitchSides(set))
            is MatchOutcome.Finished -> Outcome.MatchFinished(winner)
        }

    private fun toMatchEvent(outcome: Outcome) =
        when (outcome) {
            is Outcome.PointScored ->
                MatchEvent.PointScored(winnerSide = sideConfig.getSide(outcome.winner), withSideSwitch = outcome.withSideSwitch)
            is Outcome.GameFinished ->
                MatchEvent.GameFinished(
                    winnerSide = sideConfig.getSide(outcome.winner),
                    lhsGames = sideConfig.selectLhs(p1 = set.player1Games, p2 = set.player2Games),
                    rhsGames = sideConfig.selectRhs(p1 = set.player1Games, p2 = set.player2Games),
                    withSideSwitch = outcome.withSideSwitch,
                )
            is Outcome.SetFinished ->
                MatchEvent.SetFinished(
                    winnerSide = sideConfig.getSide(outcome.winner),
                    lhsSets = sideConfig.selectLhs(p1 = match.player1Sets, p2 = match.player2Sets),
                    rhsSets = sideConfig.selectRhs(p1 = match.player1Sets, p2 = match.player2Sets),
                    withSideSwitch = outcome.withSideSwitch,
                )
            is Outcome.MatchFinished ->
                MatchEvent.MatchFinished(
                    winnerSide = sideConfig.getSide(outcome.winner),
                    lhsSets = sideConfig.selectLhs(p1 = match.player1Sets, p2 = match.player2Sets),
                    rhsSets = sideConfig.selectRhs(p1 = match.player1Sets, p2 = match.player2Sets),
                )
        }

    private fun nextSideConfig(outcome: Outcome) =
        when (outcome) {
            is Outcome.PointScored if outcome.withSideSwitch -> sideConfig.switch()
            is Outcome.GameFinished if outcome.withSideSwitch -> sideConfig.switch()
            is Outcome.SetFinished if outcome.withSideSwitch -> sideConfig.switch()
            else -> sideConfig
        }

    private fun performTransition(outcome: Outcome) {
        when (outcome) {
            is Outcome.GameFinished -> {
                game = if (outcome.toTieBreak) TieBreakGame() else RegularGame()
            }
            is Outcome.SetFinished -> {
                saveSetToHistory(outcome.winner)
                game = RegularGame()
                set = Set()
            }
            is Outcome.MatchFinished -> {
                saveSetToHistory(outcome.winner)
                isOngoing = false
            }
            else -> {}
        }
    }

    private fun shouldSwitchSides(game: Game): Boolean {
        val tieBreak = game as? TieBreakGame
        if (tieBreak != null) {
            return (tieBreak.player1 + tieBreak.player2) % 6 == 0
        }
        return false
    }

    private fun shouldSwitchSides(set: Set): Boolean {
        return (set.player1Games + set.player2Games) % 2 == 1
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

private fun Game.toState(sideConfig: SideConfig) =
    when (this) {
        is RegularGame -> {
            when (val current = phase) {
                is Phase.Main ->
                    GameState.Regular.Main(
                        lhsPoints =
                            sideConfig.selectLhs(p1 = current.player1, p2 = current.player2),
                        rhsPoints =
                            sideConfig.selectRhs(p1 = current.player1, p2 = current.player2),
                    )

                is Phase.Deuce -> GameState.Regular.Deuce
                is Phase.Advantage ->
                    when (sideConfig.getSide(current.player)) {
                        Side.LHS -> GameState.Regular.Advantage(Side.LHS)
                        Side.RHS -> GameState.Regular.Advantage(Side.RHS)
                    }
            }
        }
        is TieBreakGame -> {
            GameState.TieBreak(
                lhsPoints = sideConfig.selectLhs(p1 = player1, p2 = player2),
                rhsPoints = sideConfig.selectRhs(p1 = player1, p2 = player2),
            )
        }
    }

private sealed interface Outcome {
    data class PointScored(val winner: Player, val withSideSwitch: Boolean) : Outcome

    data class GameFinished(val winner: Player, val toTieBreak: Boolean, val withSideSwitch: Boolean) : Outcome

    data class SetFinished(val winner: Player, val withSideSwitch: Boolean) : Outcome

    data class MatchFinished(val winner: Player) : Outcome
}

private fun SideConfig.switch() = SideConfig(playerOnLhs = this.playerOnLhs.opposite())
