package dev.jazalewski1.matchpoint.domain.tennis

class MatchControllerImpl : MatchController {
    private var state = MatchState(game = GameState.default())

    override fun getState() = state

    override fun addLhsScore() {
        state =
            when (val game = state.game) {
                is GameState.Ongoing -> {
                    when (val newLhs = game.lhs.next()) {
                        null -> {
                            state // TODO: win
                        }
                        else -> {
                            if (newLhs == Points.FORTY && game.rhs == Points.FORTY) {
                                state.copy(game = GameState.Deuce)
                            } else {
                                state.copy(game = game.copy(lhs = newLhs))
                            }
                        }
                    }
                }
                is GameState.Deuce -> {
                    state.copy(game = GameState.Advantage.Lhs)
                }
                is GameState.Advantage.Lhs -> {
                    state // TODO: win
                }
                is GameState.Advantage.Rhs -> {
                    state.copy(game = GameState.Deuce)
                }
            }
    }

    override fun addRhsScore() {
        state =
            when (val game = state.game) {
                is GameState.Ongoing -> {
                    when (val newRhs = game.rhs.next()) {
                        null -> {
                            state // TODO: win
                        }
                        else -> {
                            if (newRhs == Points.FORTY && game.lhs == Points.FORTY) {
                                state.copy(game = GameState.Deuce)
                            } else {
                                state.copy(game = game.copy(rhs = newRhs))
                            }
                        }
                    }
                }
                is GameState.Deuce -> {
                    state.copy(game = GameState.Advantage.Rhs)
                }
                is GameState.Advantage.Lhs -> {
                    state.copy(game = GameState.Deuce)
                }
                is GameState.Advantage.Rhs -> {
                    state // TODO: win
                }
            }
    }
}
