package dev.jazalewski1.matchpoint.domain.tennis

interface MatchController {
    fun getCurrentGame(): GameState

    fun addPointToLhs(): MatchEvent

    fun addPointToRhs(): MatchEvent
}
