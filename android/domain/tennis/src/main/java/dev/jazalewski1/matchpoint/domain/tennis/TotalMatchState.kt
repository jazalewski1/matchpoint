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

data class SetState(
    val lhsGames: Int,
    val rhsGames: Int,
) {
    companion object {
        fun default() = SetState(lhsGames = 0, rhsGames = 0)
    }
}

data class MatchState(
    val lhsSets: Int,
    val rhsSets: Int,
) {
    companion object {
        fun default() = MatchState(lhsSets = 0, rhsSets = 0)
    }
}

data class TotalMatchState(
    val game: GameState,
    val set: SetState,
    val match: MatchState,
)
