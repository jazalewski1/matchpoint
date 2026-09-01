package dev.jazalewski1.matchpoint.feature.match.testfakes

import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchControllerFactory

class FakeMatchControllerFactory(private val matchController: MatchController) : MatchControllerFactory {
    var storedNumOfSetsToWin: Int? = null
        private set
    override fun create(numOfSetsToWin: Int): MatchController {
        storedNumOfSetsToWin = numOfSetsToWin
        return matchController
    }
}