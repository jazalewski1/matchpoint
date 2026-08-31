package dev.jazalewski1.matchpoint.feature.match.testfakes

import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchHistory
import dev.jazalewski1.matchpoint.domain.tennis.TotalMatchState
import dev.jazalewski1.matchpoint.feature.match.testdata.*

class FakeMatchController : MatchController {
    var addPointToLhsCount = 0
        private set

    var addPointToRhsCount = 0
        private set

    private var totalMatchState: TotalMatchState =
        TotalMatchState(
            game = gameLoveAll,
            set = set0To0,
            match = match0To0,
        )
    private var lhsMatchEvent: MatchEvent = MatchEvent.PointScored
    private var rhsMatchEvent: MatchEvent = MatchEvent.PointScored
    private var matchHistory: MatchHistory = MatchHistory(sets = listOf())

    private var addPointToLhsCallback: (() -> Unit)? = null
    private var addPointToRhsCallback: (() -> Unit)? = null

    fun returnGetState(new: TotalMatchState) {
        totalMatchState = new
    }

    fun returnAddPointToLhs(event: MatchEvent) {
        lhsMatchEvent = event
    }

    fun returnAddPointToRhs(event: MatchEvent) {
        rhsMatchEvent = event
    }

    fun returnGetHistory(new: MatchHistory) {
        matchHistory = new
    }

    fun afterAddPointToLhs(callback: () -> Unit) {
        addPointToLhsCallback = callback
    }

    fun afterAddPointToRhs(callback: () -> Unit) {
        addPointToRhsCallback = callback
    }

    override fun getState(): TotalMatchState = totalMatchState

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

    override fun getHistory(): MatchHistory = matchHistory
}
