package dev.jazalewski1.matchpoint.domain.tennis

interface MatchController {
    fun getCurrentGame(): GameState

    fun addPointToLhs(): PointOutcome

    fun addPointToRhs(): PointOutcome
}
