package dev.jazalewski1.matchpoint.domain.tennis

class MatchControllerImpl : MatchController {
    private val game = Game()

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

    override fun addPointToLhs(): MatchEvent {
        return when (game.addPointToPlayer1()) {
            is GameOutcome.PointScored -> MatchEvent.PointScored
            is GameOutcome.Finished -> {
                game.reset()
                MatchEvent.GameWon
            }
        }
    }

    override fun addPointToRhs(): MatchEvent {
        return when (game.addPointToPlayer2()) {
            is GameOutcome.PointScored -> MatchEvent.PointScored
            is GameOutcome.Finished -> {
                game.reset()
                MatchEvent.GameWon
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

    fun addPointToPlayer1(): GameOutcome = addPoint(Player.ONE)

    fun addPointToPlayer2(): GameOutcome = addPoint(Player.TWO)

    fun reset() {
        state = State.Regular(Points.LOVE, Points.LOVE)
    }

    private fun addPoint(player: Player): GameOutcome {
        state.next(pointWinner = player)?.let { nextState ->
            state = nextState
            return GameOutcome.PointScored(winner = player)
        }
        return GameOutcome.Finished(winner = player)
    }
}
