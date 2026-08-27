package dev.jazalewski1.matchpoint.feature.match.testfakes

import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchState
import dev.jazalewski1.matchpoint.feature.match.testdata.*

class FakeMatchController : MatchController {
    var addPointToLhsCount = 0
        private set

    var addPointToRhsCount = 0
        private set

    private var matchState: MatchState =
        MatchState(
            game = gameLoveAll,
            set = set0To0,
            lhsSets = 0,
            rhsSets = 0,
        )
    private var lhsMatchEvent: MatchEvent = MatchEvent.PointScored
    private var rhsMatchEvent: MatchEvent = MatchEvent.PointScored

    private var addPointToLhsCallback: (() -> Unit)? = null
    private var addPointToRhsCallback: (() -> Unit)? = null

    fun returnGetState(new: MatchState) {
        matchState = new
    }

    fun returnAddPointToLhs(event: MatchEvent) {
        lhsMatchEvent = event
    }

    fun returnAddPointToRhs(event: MatchEvent) {
        rhsMatchEvent = event
    }

    fun afterAddPointToLhs(callback: () -> Unit) {
        addPointToLhsCallback = callback
    }

    fun afterAddPointToRhs(callback: () -> Unit) {
        addPointToRhsCallback = callback
    }

    override fun getState(): MatchState = matchState

    override fun addPointToLhs(): MatchEvent {
        addPointToLhsCount += 1
        addPointToLhsCallback?.invoke()
        return lhsMatchEvent
    }

    override fun addPointToRhs(): MatchEvent {
        addPointToRhsCount += 1
        addPointToRhsCallback?.invoke()
        return rhsMatchEvent
    }
}
