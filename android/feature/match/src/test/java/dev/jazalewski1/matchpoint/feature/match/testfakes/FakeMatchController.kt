package dev.jazalewski1.matchpoint.feature.match.testfakes

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.SetState
import dev.jazalewski1.matchpoint.feature.match.testdata.*

class FakeMatchController : MatchController {
    var addPointToLhsCount = 0
        private set

    var addPointToRhsCount = 0
        private set

    private var gameState: GameState = gameLoveAll
    private var setState: SetState = set0To0
    private var lhsMatchEvent: MatchEvent = MatchEvent.PointScored
    private var rhsMatchEvent: MatchEvent = MatchEvent.PointScored

    fun returnGetCurrentGame(new: GameState) {
        gameState = new
    }

    fun returnGetCurrentSet(new: SetState) {
        setState = new
    }

    fun returnAddPointToLhs(event: MatchEvent) {
        lhsMatchEvent = event
    }

    fun returnAddPointToRhs(event: MatchEvent) {
        rhsMatchEvent = event
    }

    override fun getCurrentGame() = gameState

    override fun getCurrentSet() = setState

    override fun addPointToLhs(): MatchEvent {
        addPointToLhsCount += 1
        return lhsMatchEvent
    }

    override fun addPointToRhs(): MatchEvent {
        addPointToRhsCount += 1
        return rhsMatchEvent
    }
}
