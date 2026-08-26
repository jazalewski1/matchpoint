package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.domain.tennis.RegularGame.State

class MatchControllerImpl : MatchController {
    private var game: Game = RegularGame()
    private val set = Set()

    override fun getCurrentGame(): GameState = game.toState()

    override fun addPointToLhs(): MatchEvent = processPointScored(Player.ONE)

    override fun addPointToRhs(): MatchEvent = processPointScored(Player.TWO)

    private fun processPointScored(winner: Player): MatchEvent =
        when (val gameOutcome = game.addPoint(winner)) {
            is GameOutcome.PointScored -> MatchEvent.PointScored
            is GameOutcome.Finished ->
                processGameFinished(winner = winner, isTieBreak = gameOutcome.isTieBreak)
        }

    private fun processGameFinished(winner: Player, isTieBreak: Boolean): MatchEvent {
        if (isTieBreak) {
            game = RegularGame()
            return MatchEvent.SetWon
        }
        when (set.addGame(winner)) {
            is SetOutcome.None -> {
                game = RegularGame()
                return MatchEvent.GameWon
            }
            is SetOutcome.Finished -> {
                game = RegularGame()
                set.reset()
                return MatchEvent.SetWon
            }
            is SetOutcome.Tiebreak -> {
                game = TieBreakGame()
                return MatchEvent.GameWon
            }
        }
    }
}

private enum class Player {
    ONE,
    TWO,
}

private sealed interface GameOutcome {
    data class PointScored(val winner: Player) : GameOutcome

    data class Finished(val winner: Player, val isTieBreak: Boolean) : GameOutcome
}

private sealed interface Game {
    fun addPoint(winner: Player): GameOutcome

    fun toState(): GameState
}

private class RegularGame : Game {
    sealed interface State {
        fun next(pointWinner: Player): State?

        data class Main(val player1: Points, val player2: Points) : State {
            override fun next(pointWinner: Player): State? =
                when (pointWinner) {
                    Player.ONE -> {
                        player1.next()?.let { nextPlayer1 ->
                            if (nextPlayer1 == Points.FORTY && player2 == Points.FORTY) {
                                return Deuce
                            }
                            this.copy(player1 = nextPlayer1)
                        }
                    }
                    Player.TWO -> {
                        player2.next()?.let { nextPlayer2 ->
                            if (nextPlayer2 == Points.FORTY && player1 == Points.FORTY) {
                                return Deuce
                            }
                            this.copy(player2 = nextPlayer2)
                        }
                    }
                }
        }

        data object Deuce : State {
            override fun next(pointWinner: Player) = Advantage(player = pointWinner)
        }

        data class Advantage(val player: Player) : State {
            override fun next(pointWinner: Player): State? {
                if (pointWinner == player) {
                    return null
                }
                return Deuce
            }
        }
    }

    private var state: State = State.Main(Points.LOVE, Points.LOVE)

    override fun addPoint(winner: Player): GameOutcome {
        state.next(pointWinner = winner)?.let { nextState ->
            state = nextState
            return GameOutcome.PointScored(winner = winner)
        }
        return GameOutcome.Finished(winner = winner, isTieBreak = false)
    }

    override fun toState(): GameState =
        when (val current = state) {
            is State.Main ->
                GameState.Regular.Main(
                    lhs = current.player1,
                    rhs = current.player2,
                )
            is State.Deuce -> GameState.Regular.Deuce
            is State.Advantage ->
                when (current.player) {
                    Player.ONE -> GameState.Regular.Advantage.Lhs
                    Player.TWO -> GameState.Regular.Advantage.Rhs
                }
        }
}

private class TieBreakGame : Game {
    private var player1 = 0
    private var player2 = 0

    override fun addPoint(winner: Player): GameOutcome {
        when (winner) {
            Player.ONE -> player1 += 1
            Player.TWO -> player2 += 1
        }
        if (player1 >= 7 && player1 >= (player2 + 2)) {
            return GameOutcome.Finished(winner = Player.ONE, isTieBreak = true)
        }
        if (player2 >= 7 && player2 >= (player1 + 2)) {
            return GameOutcome.Finished(winner = Player.TWO, isTieBreak = true)
        }
        return GameOutcome.PointScored(winner = winner)
    }

    override fun toState(): GameState =
        GameState.TieBreak(
            lhs = player1,
            rhs = player2,
        )
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

    fun reset() {
        player1Games = 0
        player2Games = 0
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
        return SetOutcome.None
    }
}
