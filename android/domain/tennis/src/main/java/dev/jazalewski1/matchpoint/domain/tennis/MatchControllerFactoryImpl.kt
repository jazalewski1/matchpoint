package dev.jazalewski1.matchpoint.domain.tennis

class MatchControllerFactoryImpl : MatchControllerFactory {
    override fun create(numOfSetsToWin: Int): MatchController =
        MatchControllerImpl(numOfSetsToWin = numOfSetsToWin)
}
