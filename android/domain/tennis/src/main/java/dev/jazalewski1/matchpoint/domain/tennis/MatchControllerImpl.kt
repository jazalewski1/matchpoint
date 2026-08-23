package dev.jazalewski1.matchpoint.domain.tennis

class MatchControllerImpl : MatchController {
    private var gameState: GameState = GameState.default()

    override fun getCurrentGame() = gameState

    override fun addPointToLhs(): PointOutcome =
        when (val current = gameState) {
            is GameState.Ongoing -> {
                val nextLhs = current.lhs.next()
                if (nextLhs != null) {
                    if (nextLhs == Points.FORTY && current.rhs == Points.FORTY) {
                        gameState = GameState.Deuce
                        return PointOutcome.PointScored
                    } else {
                        gameState = current.copy(lhs = nextLhs)
                        return PointOutcome.PointScored
                    }
                } else {
                    gameState = GameState.default()
                    return PointOutcome.GameWon
                }
            }
            is GameState.Deuce -> {
                gameState = GameState.Advantage.Lhs
                return PointOutcome.PointScored
            }
            is GameState.Advantage.Lhs -> {
                gameState = GameState.default()
                return PointOutcome.GameWon
            }
            is GameState.Advantage.Rhs -> {
                gameState = GameState.Deuce
                return PointOutcome.PointScored
            }
        }

    override fun addPointToRhs(): PointOutcome =
        when (val current = gameState) {
            is GameState.Ongoing -> {
                val nextRhs = current.rhs.next()
                if (nextRhs != null) {
                    if (nextRhs == Points.FORTY && current.lhs == Points.FORTY) {
                        gameState = GameState.Deuce
                        return PointOutcome.PointScored
                    } else {
                        gameState = current.copy(rhs = nextRhs)
                        return PointOutcome.PointScored
                    }
                } else {
                    gameState = GameState.default()
                    return PointOutcome.GameWon
                }
            }
            is GameState.Deuce -> {
                gameState = GameState.Advantage.Rhs
                return PointOutcome.PointScored
            }
            is GameState.Advantage.Lhs -> {
                gameState = GameState.Deuce
                return PointOutcome.PointScored
            }
            is GameState.Advantage.Rhs -> {
                gameState = GameState.default()
                return PointOutcome.GameWon
            }
        }
}
