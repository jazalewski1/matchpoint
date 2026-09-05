package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.common.Side
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MatchControllerImplTest {
    @Test
    fun `has love all at beginning`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        val expected = GameState.default()
        assertThat(controller.getCurrentGameState()).isEqualTo(expected)
    }

    @Test
    fun `lhs point scored`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(MatchEvent.PointScored(winnerSide = Side.LHS, withSideSwitch = false))
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(Points.FIFTEEN, Points.LOVE))
    }

    @Test
    fun `rhs point scored`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(MatchEvent.PointScored(winnerSide = Side.RHS, withSideSwitch = false))
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(Points.LOVE, Points.FIFTEEN))
    }

    @Test
    fun `lhs wins game`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        repeat(2) { controller.addPointToRhs() }
        repeat(3) { controller.addPointToLhs() }

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.GameFinished(
                    winnerSide = Side.LHS,
                    lhsGames = 1,
                    rhsGames = 0,
                    withSideSwitch = true,
                )
            )
    }

    @Test
    fun `rhs wins game`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        repeat(2) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.GameFinished(
                    winnerSide = Side.RHS,
                    lhsGames = 0,
                    rhsGames = 1,
                    withSideSwitch = true,
                )
            )
    }

    @Test
    fun `returns regular main game state after switching sides`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        repeat(2) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.THIRTY, rhsPoints = Points.FORTY))

        controller.addPointToRhs()

        repeat(2) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.THIRTY, rhsPoints = Points.FORTY))
    }

    @Test
    fun `returns regular advantage game state after switching sides`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        advanceToDeuce(controller)
        controller.addPointToLhs()
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.LHS))

        controller.addPointToRhs()
        controller.addPointToRhs()
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.RHS))

        controller.addPointToRhs()
        // switched sides

        advanceToDeuce(controller)
        controller.addPointToLhs()
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.LHS))

        controller.addPointToRhs()
        controller.addPointToRhs()
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.RHS))
    }

    private fun advanceToDeuce(controller: MatchControllerImpl) {
        repeat(3) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
    }

    @Test
    fun `game is deuce`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        advanceToDeuce(controller)

        assertThat(controller.getCurrentGameState()).isEqualTo(GameState.Regular.Deuce)
    }

    @Test
    fun `advantage for lhs`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        advanceToDeuce(controller)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(MatchEvent.PointScored(winnerSide = Side.LHS, withSideSwitch = false))
        val expected = GameState.Regular.Advantage(Side.LHS)
        assertThat(controller.getCurrentGameState()).isEqualTo(expected)
    }

    @Test
    fun `advantage for rhs`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        advanceToDeuce(controller)

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(MatchEvent.PointScored(winnerSide = Side.RHS, withSideSwitch = false))
        val expected = GameState.Regular.Advantage(Side.RHS)
        assertThat(controller.getCurrentGameState()).isEqualTo(expected)
    }

    private fun scorePointsForLhs(controller: MatchController, numOfPoints: Int) {
        repeat(numOfPoints) { controller.addPointToLhs() }
    }

    private fun scorePointsForRhs(controller: MatchController, numOfPoints: Int) {
        repeat(numOfPoints) { controller.addPointToRhs() }
    }

    private fun winGameForLhs(controller: MatchController) {
        scorePointsForLhs(controller, numOfPoints = 4)
    }

    private fun winGameForRhs(controller: MatchController) {
        scorePointsForRhs(controller, numOfPoints = 4)
    }

    @Test
    fun `returns set finished with lhs winner`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        scorePointsForLhs(controller, numOfPoints = 3)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.SetFinished(
                    winnerSide = Side.LHS,
                    lhsSets = 1,
                    rhsSets = 0,
                    withSideSwitch = false,
                )
            )
    }

    @Test
    fun `returns set finished with rhs winner`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        scorePointsForRhs(controller, numOfPoints = 3)

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.SetFinished(
                    winnerSide = Side.RHS,
                    lhsSets = 0,
                    rhsSets = 1,
                    withSideSwitch = false,
                )
            )
    }

    @Test
    fun `returns set finished with lhs winner after odd number of games`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        scorePointsForLhs(controller, numOfPoints = 3)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.SetFinished(
                    winnerSide = Side.LHS,
                    lhsSets = 1,
                    rhsSets = 0,
                    withSideSwitch = true,
                )
            )
    }

    @Test
    fun `returns set finished with rhs winner after odd number of games`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        scorePointsForRhs(controller, numOfPoints = 3)

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.SetFinished(
                    winnerSide = Side.RHS,
                    lhsSets = 0,
                    rhsSets = 1,
                    withSideSwitch = true,
                )
            )
    }

    @Test
    fun `player1 ties to tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        scorePointsForLhs(controller, numOfPoints = 3)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.GameFinished(
                    winnerSide = Side.LHS,
                    lhsGames = 6,
                    rhsGames = 6,
                    withSideSwitch = false,
                )
            )

        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.TieBreak(lhsPoints = 0, rhsPoints = 0))
    }

    @Test
    fun `player2 ties to tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        scorePointsForRhs(controller, numOfPoints = 3)

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.GameFinished(
                    winnerSide = Side.RHS,
                    lhsGames = 6,
                    rhsGames = 6,
                    withSideSwitch = false,
                )
            )

        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.TieBreak(lhsPoints = 0, rhsPoints = 0))
    }

    private fun advanceToTieBreak(controller: MatchController) {
        repeat(3) {
            winGameForLhs(controller)
            winGameForLhs(controller)
            winGameForRhs(controller)
            winGameForRhs(controller)
        }
    }

    @Test
    fun `player1 wins tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        advanceToTieBreak(controller)
        scorePointsForLhs(controller, numOfPoints = 6)

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.SetFinished(
                    winnerSide = Side.RHS,
                    lhsSets = 0,
                    rhsSets = 1,
                    withSideSwitch = true,
                )
            )
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.LOVE, rhsPoints = Points.LOVE))
    }

    @Test
    fun `player2 wins tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        advanceToTieBreak(controller)
        scorePointsForRhs(controller, numOfPoints = 6)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(
                MatchEvent.SetFinished(
                    winnerSide = Side.LHS,
                    lhsSets = 1,
                    rhsSets = 0,
                    withSideSwitch = true,
                )
            )
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.LOVE, rhsPoints = Points.LOVE))
    }

    private fun winSetForPlayerStartingOnLhs(controller: MatchController) {
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
    }

    private fun winSetForPlayerStartingOnRhs(controller: MatchController) {
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
    }

    @Test
    fun `player1 wins match with required number of sets`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 4)

        repeat(6) {
            winSetForPlayerStartingOnLhs(controller)
        }

        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        scorePointsForRhs(controller, numOfPoints = 3)

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(MatchEvent.MatchFinished(winnerSide = Side.RHS, lhsSets = 3, rhsSets = 4))
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.LOVE, rhsPoints = Points.FORTY))
    }

    @Test
    fun `player2 wins match with required number of sets`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 4)

        repeat(6) {
            winSetForPlayerStartingOnRhs(controller)
        }

        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        scorePointsForLhs(controller, numOfPoints = 3)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(MatchEvent.MatchFinished(winnerSide = Side.LHS, lhsSets = 4, rhsSets = 3))
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.FORTY, rhsPoints = Points.LOVE))
    }

    @Test
    fun `finished match does not accept points`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 1)

        winSetForPlayerStartingOnLhs(controller)

        val lastGameState =
            GameState.Regular.Main(lhsPoints = Points.LOVE, rhsPoints = Points.FORTY)
        assertThat(controller.getCurrentGameState()).isEqualTo(lastGameState)

        assertThat(controller.addPointToLhs()).isNull()
        assertThat(controller.addPointToRhs()).isNull()

        assertThat(controller.getCurrentGameState()).isEqualTo(lastGameState)
    }

    @Test
    fun `returns match history`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        // set 1
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)

        // set 2
        advanceToTieBreak(controller)
        scorePointsForRhs(controller, numOfPoints = 6)
        scorePointsForLhs(controller, numOfPoints = 1)

        // set 3
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForRhs(controller)
        winGameForLhs(controller)
        winGameForLhs(controller)
        winGameForRhs(controller)

        val expected =
            MatchHistory(
                sets =
                    listOf(
                        MatchHistory.Set(
                            player1Games = 6,
                            player2Games = 4,
                            winner = Player.ONE,
                        ),
                        MatchHistory.Set(
                            player1Games = 7,
                            player2Games = 6,
                            winner = Player.ONE,
                            tieBreak =
                                MatchHistory.Set.TieBreak(
                                    player1Points = 7,
                                    player2Points = 0,
                                ),
                        ),
                        MatchHistory.Set(
                            player1Games = 6,
                            player2Games = 2,
                            winner = Player.ONE,
                        ),
                    )
            )
        assertThat(controller.getHistory()).isEqualTo(expected)
    }

    private fun assertPlayer1OnLhs(controller: MatchController) {
        assertThat(controller.getSideConfig().playerOnLhs).isEqualTo(Player.ONE)
    }

    private fun assertPlayer2OnLhs(controller: MatchController) {
        assertThat(controller.getSideConfig().playerOnLhs).isEqualTo(Player.TWO)
    }

    @Test
    fun `switches sides after every odd game`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        assertPlayer1OnLhs(controller)

        winGameForLhs(controller)
        assertPlayer2OnLhs(controller)

        winGameForRhs(controller)
        assertPlayer2OnLhs(controller)

        winGameForLhs(controller)
        assertPlayer1OnLhs(controller)

        winGameForRhs(controller)
        assertPlayer1OnLhs(controller)

        winGameForRhs(controller)
        winGameForLhs(controller)
        assertPlayer2OnLhs(controller)

        winGameForLhs(controller)
        winGameForRhs(controller)
        assertPlayer1OnLhs(controller)
    }

    @Test
    fun `switches sides at the end of set if number of games is odd`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)
        // P1:0 - P2:0
        winGameForRhs(controller) // P2:1 - P1:0 (switched)
        winGameForRhs(controller) // P2:1 - P1:1
        winGameForRhs(controller) // P1:2 - P2:1 (switched)
        winGameForLhs(controller) // P1:3 - P2:1
        winGameForLhs(controller) // P2:1 - P1:4 (switched)
        winGameForRhs(controller) // P2:1 - P1:5
        assertPlayer2OnLhs(controller)
        winGameForRhs(controller) // P1:6 - P2:1 (switched)
        assertPlayer1OnLhs(controller)
        // set
        // P1:0 - P2:0
        winGameForLhs(controller) // P2:0 - P1:1 (switched)
        assertPlayer2OnLhs(controller)
    }

    @Test
    fun `does not switch sides at the end of set if number of games is even`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        // P1:0 - P2:0
        winGameForLhs(controller) // P2:0 - P1:1 (switched)
        winGameForRhs(controller) // P2:0 - P1:2
        winGameForRhs(controller) // P1:3 - P2:0 (switched)
        winGameForLhs(controller) // P1:4 - P2:0
        winGameForLhs(controller) // P2:0 - P1:5 (switched)
        assertPlayer2OnLhs(controller)
        winGameForRhs(controller) // P2:0 - P1:6
        assertPlayer2OnLhs(controller)
        // set
        // P2:0 - P1:0
        winGameForLhs(controller) // P1:0 - P2:1 (switched)
        assertPlayer1OnLhs(controller)
    }

    @Test
    fun `switches sides after every 6th point in a tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        advanceToTieBreak(controller)

        assertPlayer1OnLhs(controller)
        scorePointsForLhs(controller, numOfPoints = 1)
        assertPlayer1OnLhs(controller)
        scorePointsForRhs(controller, numOfPoints = 1)
        assertPlayer1OnLhs(controller)
        scorePointsForLhs(controller, numOfPoints = 2)
        scorePointsForRhs(controller, numOfPoints = 2)
        assertPlayer2OnLhs(controller)
        repeat(3) {
            scorePointsForLhs(controller, numOfPoints = 1)
            scorePointsForRhs(controller, numOfPoints = 1)
        }
        assertPlayer1OnLhs(controller)
        repeat(3) {
            scorePointsForLhs(controller, numOfPoints = 1)
            scorePointsForRhs(controller, numOfPoints = 1)
        }
        assertPlayer2OnLhs(controller)
    }

    @Test
    fun `switches sides after tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        advanceToTieBreak(controller)

        assertPlayer1OnLhs(controller)
        scorePointsForLhs(controller, numOfPoints = 6)
        assertPlayer2OnLhs(controller)
        scorePointsForRhs(controller, numOfPoints = 1)
        assertPlayer1OnLhs(controller)
    }
}
