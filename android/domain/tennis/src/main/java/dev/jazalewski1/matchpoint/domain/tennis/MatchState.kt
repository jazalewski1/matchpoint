package dev.jazalewski1.matchpoint.domain.tennis

data class MatchState(val game: GameState)

sealed interface GameState {
    data class Ongoing(val lhs: Points, val rhs: Points) : GameState

    object Deuce : GameState

    sealed interface Advantage : GameState {
        data object Lhs : Advantage

        data object Rhs : Advantage
    }

    companion object {
        fun default() = Ongoing(lhs = Points.LOVE, rhs = Points.LOVE)
    }
}
