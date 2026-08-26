package dev.jazalewski1.matchpoint.domain.tennis

sealed interface GameState {
    sealed interface Regular : GameState {
        data class Main(val lhs: Points, val rhs: Points) : Regular

        object Deuce : Regular

        sealed interface Advantage : Regular {
            data object Lhs : Advantage

            data object Rhs : Advantage
        }
    }

    data class TieBreak(val lhs: Int, val rhs: Int) : GameState

    companion object {
        fun default() = Regular.Main(lhs = Points.LOVE, rhs = Points.LOVE)
    }
}
