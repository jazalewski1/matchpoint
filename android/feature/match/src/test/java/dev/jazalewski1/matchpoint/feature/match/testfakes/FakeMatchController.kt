package dev.jazalewski1.matchpoint.feature.match.testfakes

import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchHistory
import dev.jazalewski1.matchpoint.domain.tennis.SideConfig
import dev.jazalewski1.matchpoint.feature.match.testdata.*

class FakeMatchController : MatchController {
    var addPointToLhsCount = 0
        private set

    var addPointToRhsCount = 0
        private set

    private var gameState: GameState = gameLoveAll
    private var sideConfig: SideConfig = SideConfig(playerOnLhs = Player.ONE)
    private var lhsMatchEvent: MatchEvent = MatchEvent.PointScored(winnerSide = Side.LHS)
    private var rhsMatchEvent: MatchEvent = MatchEvent.PointScored(winnerSide = Side.RHS)
    private var matchHistory: MatchHistory = MatchHistory(sets = listOf())

    private var addPointToLhsCallback: (() -> Unit)? = null
    private var addPointToRhsCallback: (() -> Unit)? = null

    fun returnGetCurrentGameState(new: GameState) {
        gameState = new
    }

    fun returnGetSideConfig(new: SideConfig) {
        sideConfig = new
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

    override fun getCurrentGameState(): GameState = gameState

    override fun getSideConfig(): SideConfig = sideConfig

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
