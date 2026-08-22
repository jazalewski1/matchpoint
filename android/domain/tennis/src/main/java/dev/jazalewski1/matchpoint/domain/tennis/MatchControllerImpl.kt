package dev.jazalewski1.matchpoint.domain.tennis

class MatchControllerImpl : MatchController {
    private var state = MatchState(game = GameState.default())

    override fun getState() = state

    override fun addLhsScore(): PointOutcome =
        when (val game = state.game) {
            is GameState.Ongoing -> {
                val nextLhs = game.lhs.next()
                if (nextLhs != null) {
                    if (nextLhs == Points.FORTY && game.rhs == Points.FORTY) {
                        state = state.copy(game = GameState.Deuce)
                        return PointOutcome.PointScored(Side.LHS)
                    } else {
                        state = state.copy(game = game.copy(lhs = nextLhs))
                        return PointOutcome.PointScored(Side.LHS)
                    }
                } else {
                    state = state.copy(game = GameState.default())
                    return PointOutcome.GameWon(Side.LHS)
                }
            }
            is GameState.Deuce -> {
                state = state.copy(game = GameState.Advantage.Lhs)
                return PointOutcome.PointScored(Side.LHS)
            }
            is GameState.Advantage.Lhs -> {
                state = state.copy(game = GameState.default())
                return PointOutcome.GameWon(Side.LHS)
            }
            is GameState.Advantage.Rhs -> {
                state = state.copy(game = GameState.Deuce)
                return PointOutcome.PointScored(Side.LHS)
            }
        }

    override fun addRhsScore(): PointOutcome =
        when (val game = state.game) {
            is GameState.Ongoing -> {
                val nextRhs = game.rhs.next()
                if (nextRhs != null) {
                    if (nextRhs == Points.FORTY && game.lhs == Points.FORTY) {
                        state = state.copy(game = GameState.Deuce)
                        return PointOutcome.PointScored(Side.RHS)
                    } else {
                        state = state.copy(game = game.copy(rhs = nextRhs))
                        return PointOutcome.PointScored(Side.RHS)
                    }
                } else {
                    state = state.copy(game = GameState.default())
                    return PointOutcome.GameWon(Side.RHS)
                }
            }
            is GameState.Deuce -> {
                state = state.copy(game = GameState.Advantage.Rhs)
                return PointOutcome.PointScored(Side.RHS)
            }
            is GameState.Advantage.Lhs -> {
                state = state.copy(game = GameState.Deuce)
                return PointOutcome.PointScored(Side.RHS)
            }
            is GameState.Advantage.Rhs -> {
                state = state.copy(game = GameState.default())
                return PointOutcome.GameWon(Side.RHS)
            }
        }
}
