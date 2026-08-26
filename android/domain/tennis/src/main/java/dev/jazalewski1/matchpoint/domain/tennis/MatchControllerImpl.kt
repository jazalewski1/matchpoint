package dev.jazalewski1.matchpoint.domain.tennis

class MatchControllerImpl : MatchController {
    private val game = Game()
    private val set = Set()

    override fun getCurrentGame() =
        when (val gameState = game.state) {
            is Game.State.Regular ->
                GameState.Regular(
                    lhs = gameState.player1,
                    rhs = gameState.player2,
                )
            is Game.State.Deuce -> GameState.Deuce
            is Game.State.Advantage ->
                when (gameState.player) {
                    Player.ONE -> GameState.Advantage.Lhs
                    Player.TWO -> GameState.Advantage.Rhs
                }
        }

    override fun addPointToLhs(): MatchEvent = processPointScored(Player.ONE)

    override fun addPointToRhs(): MatchEvent = processPointScored(Player.TWO)

    private fun processPointScored(winner: Player): MatchEvent =
        when (game.addPoint(winner)) {
            is GameOutcome.PointScored -> MatchEvent.PointScored
            is GameOutcome.Finished -> processGameFinished(winner)
        }

    private fun processGameFinished(winner: Player): MatchEvent {
        game.reset()
        when (set.addGame(winner)) {
            is SetOutcome.None -> return MatchEvent.GameWon
            is SetOutcome.Finished -> {
                set.reset()
                return MatchEvent.SetWon
            }
            is SetOutcome.Tiebreak -> {
                TODO()
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

    data class Finished(val winner: Player) : GameOutcome
}

private class Game {
    sealed interface State {
        fun next(pointWinner: Player): State?

        data class Regular(val player1: Points, val player2: Points) : State {
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

    var state: State = State.Regular(Points.LOVE, Points.LOVE)
        private set

    fun addPoint(player: Player): GameOutcome {
        state.next(pointWinner = player)?.let { nextState ->
            state = nextState
            return GameOutcome.PointScored(winner = player)
        }
        return GameOutcome.Finished(winner = player)
    }

    fun reset() {
        state = State.Regular(Points.LOVE, Points.LOVE)
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
