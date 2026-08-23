package dev.jazalewski1.matchpoint.domain.tennis

sealed interface GameState {
    data class Regular(val lhs: Points, val rhs: Points) : GameState

    object Deuce : GameState

    sealed interface Advantage : GameState {
        data object Lhs : Advantage

        data object Rhs : Advantage
    }

    companion object {
        fun default() = Regular(lhs = Points.LOVE, rhs = Points.LOVE)
    }
}
