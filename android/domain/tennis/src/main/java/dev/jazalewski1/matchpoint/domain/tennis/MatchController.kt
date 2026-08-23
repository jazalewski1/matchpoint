package dev.jazalewski1.matchpoint.domain.tennis

interface MatchController {
    fun getState(): MatchState

    fun addPointToLhs(): PointOutcome

    fun addPointToRhs(): PointOutcome
}
