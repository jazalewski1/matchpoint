package dev.jazalewski1.matchpoint.domain.tennis

interface MatchController {
    fun getCurrentGameState(): GameState

    fun addPointToLhs(): MatchEvent

    fun addPointToRhs(): MatchEvent

    fun getHistory(): MatchHistory
}
