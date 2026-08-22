package dev.jazalewski1.matchpoint.domain.tennis

enum class Points(val value: Int) {
    LOVE(0) {
        override fun next() = FIFTEEN
    },
    FIFTEEN(15) {
        override fun next() = THIRTY
    },
    THIRTY(30) {
        override fun next() = FORTY
    },
    FORTY(40) {
        override fun next() = null
    };

    abstract fun next(): Points?
}

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

data class MatchState(val game: GameState)

enum class Side {
    LHS,
    RHS,
}

sealed interface PointOutcome {
    data class PointScored(val side: Side) : PointOutcome

    data class GameWon(val side: Side) : PointOutcome
}

interface MatchController {
    fun getState(): MatchState

    fun addLhsScore(): PointOutcome

    fun addRhsScore(): PointOutcome
}
