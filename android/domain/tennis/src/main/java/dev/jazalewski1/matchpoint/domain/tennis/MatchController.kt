package dev.jazalewski1.matchpoint.domain.tennis

interface MatchController {
    fun getState(): TotalMatchState

    fun addPointToLhs(): MatchEvent

    fun addPointToRhs(): MatchEvent

    fun getHistory(): MatchHistory
}
