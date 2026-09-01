package dev.jazalewski1.matchpoint.domain.tennis

interface MatchControllerFactory {
    fun create(numOfSetsToWin: Int): MatchController
}
