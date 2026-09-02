package dev.jazalewski1.matchpoint.domain.tennis

sealed interface GameState {
    sealed interface Regular : GameState {
        data class Main(val lhsPoints: Points, val rhsPoints: Points) : Regular

        object Deuce : Regular

        sealed interface Advantage : Regular {
            data object Lhs : Advantage

            data object Rhs : Advantage
        }
    }

    data class TieBreak(val lhsPoints: Int, val rhsPoints: Int) : GameState

    companion object {
        fun default() = Regular.Main(lhsPoints = Points.LOVE, rhsPoints = Points.LOVE)
    }
}

data class TotalMatchState( // TODO: rename to MatchState, or remove if not needed anymore
    val game: GameState
)
