package dev.jazalewski1.matchpoint.feature.match.testfakes

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.PointOutcome
import dev.jazalewski1.matchpoint.feature.match.testdata.*

class FakeMatchController : MatchController {
    var addPointToLhsCount = 0
        private set

    var addPointToRhsCount = 0
        private set

    private var gameState: GameState = gameLoveAll
    private var lhsPointOutcome: PointOutcome = PointOutcome.PointScored
    private var rhsPointOutcome: PointOutcome = PointOutcome.PointScored

    fun returnGetCurrentGame(new: GameState) {
        gameState = new
    }

    fun returnAddPointToLhs(outcome: PointOutcome) {
        lhsPointOutcome = outcome
    }

    fun returnAddPointToRhs(outcome: PointOutcome) {
        rhsPointOutcome = outcome
    }

    override fun getCurrentGame() = gameState

    override fun addPointToLhs(): PointOutcome {
        addPointToLhsCount += 1
        return lhsPointOutcome
    }

    override fun addPointToRhs(): PointOutcome {
        addPointToRhsCount += 1
        return rhsPointOutcome
    }
}
