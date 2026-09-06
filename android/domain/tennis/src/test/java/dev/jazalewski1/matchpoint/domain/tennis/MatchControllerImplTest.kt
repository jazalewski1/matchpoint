package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.common.Side
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

private val lhs = Side.LHS
private val rhs = Side.RHS

class MatchControllerImplTest {
    private fun MatchController.scorePointsForLhs(numOfPoints: Int) {
        repeat(numOfPoints) { addPointToLhs() }
    }

    private fun MatchController.scorePointsForRhs(numOfPoints: Int) {
        repeat(numOfPoints) { addPointToRhs() }
    }

    private fun MatchController.winPoints(vararg sides: Side) {
        sides.forEach { side -> if (side == Side.LHS) addPointToLhs() else addPointToRhs() }
    }

    private fun MatchController.winGameForLhs() = scorePointsForLhs(numOfPoints = 4)

    private fun MatchController.winGameForRhs() = scorePointsForRhs(numOfPoints = 4)

    private fun MatchController.winGames(vararg sides: Side) {
        sides.forEach { side -> if (side == Side.LHS) winGameForLhs() else winGameForRhs() }
    }

    private fun MatchController.winSetForPlayerStartingOnLhs() =
        winGames(lhs, rhs, rhs, lhs, lhs, rhs)

    private fun MatchController.winSetForPlayerStartingOnRhs() =
        winGames(rhs, lhs, lhs, rhs, rhs, lhs)

    private fun MatchController.simulateDeuce() {
        repeat(3) { winPoints(lhs, rhs) }
    }

    private fun MatchController.simulateTieBreak() {
        repeat(3) { winGames(lhs, lhs, rhs, rhs) }
    }

    private fun assertPlayer1OnLhs(controller: MatchController) {
        assertThat(controller.getSideConfig().playerOnLhs).isEqualTo(Player.ONE)
    }

    private fun assertPlayer2OnLhs(controller: MatchController) {
        assertThat(controller.getSideConfig().playerOnLhs).isEqualTo(Player.TWO)
    }

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

        controller.winPoints(rhs, rhs, lhs, lhs, lhs)

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

        controller.winPoints(lhs, lhs, rhs, rhs, rhs)

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

        controller.winPoints(lhs, lhs, rhs, rhs, rhs)
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.THIRTY, rhsPoints = Points.FORTY))

        controller.addPointToRhs()

        controller.winPoints(lhs, lhs, rhs, rhs, rhs)
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.THIRTY, rhsPoints = Points.FORTY))
    }

    @Test
    fun `returns regular advantage game state after switching sides`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        controller.simulateDeuce()
        controller.addPointToLhs()
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.LHS))

        controller.winPoints(rhs, rhs)
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.RHS))

        controller.addPointToRhs()
        // switched sides

        controller.simulateDeuce()
        controller.addPointToLhs()
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.LHS))

        controller.winPoints(rhs, rhs)
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Advantage(Side.RHS))
    }

    @Test
    fun `game is deuce`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        controller.simulateDeuce()

        assertThat(controller.getCurrentGameState()).isEqualTo(GameState.Regular.Deuce)
    }

    @Test
    fun `advantage for lhs`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        controller.simulateDeuce()

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(MatchEvent.PointScored(winnerSide = Side.LHS, withSideSwitch = false))
        val expected = GameState.Regular.Advantage(Side.LHS)
        assertThat(controller.getCurrentGameState()).isEqualTo(expected)
    }

    @Test
    fun `advantage for rhs`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        controller.simulateDeuce()

        val event = controller.addPointToRhs()
        assertThat(event)
            .isEqualTo(MatchEvent.PointScored(winnerSide = Side.RHS, withSideSwitch = false))
        val expected = GameState.Regular.Advantage(Side.RHS)
        assertThat(controller.getCurrentGameState()).isEqualTo(expected)
    }

    @Test
    fun `returns set finished with lhs winner`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        controller.winGames(rhs, lhs, lhs, rhs, rhs)
        controller.scorePointsForLhs(numOfPoints = 3)

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

        controller.winGames(lhs, rhs, rhs, lhs, lhs)
        controller.scorePointsForRhs(numOfPoints = 3)

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

        controller.winGames(lhs, lhs, lhs, rhs, rhs, lhs)
        controller.scorePointsForLhs(numOfPoints = 3)

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

        controller.winGames(rhs, rhs, rhs, lhs, lhs, rhs)
        controller.scorePointsForRhs(numOfPoints = 3)

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

        controller.winGames(rhs, rhs, lhs, lhs, rhs, rhs, lhs, lhs, rhs, rhs, lhs)
        controller.scorePointsForLhs(numOfPoints = 3)

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

        controller.winGames(lhs, lhs, rhs, rhs, lhs, lhs, rhs, rhs, lhs, lhs, rhs)
        controller.scorePointsForRhs(numOfPoints = 3)

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

    @Test
    fun `player1 wins tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 3)

        controller.simulateTieBreak()
        controller.scorePointsForLhs(numOfPoints = 6)

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

        controller.simulateTieBreak()
        controller.scorePointsForRhs(numOfPoints = 6)

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

    @Test
    fun `player1 wins match with required number of sets`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 4)

        repeat(6) {
            controller.winSetForPlayerStartingOnLhs()
        }

        controller.winGames(lhs, rhs, rhs, lhs, lhs)
        controller.scorePointsForRhs(numOfPoints = 3)

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
            controller.winSetForPlayerStartingOnRhs()
        }

        controller.winGames(rhs, lhs, lhs, rhs, rhs)
        controller.scorePointsForLhs(numOfPoints = 3)

        val event = controller.addPointToLhs()
        assertThat(event)
            .isEqualTo(MatchEvent.MatchFinished(winnerSide = Side.LHS, lhsSets = 4, rhsSets = 3))
        assertThat(controller.getCurrentGameState())
            .isEqualTo(GameState.Regular.Main(lhsPoints = Points.FORTY, rhsPoints = Points.LOVE))
    }

    @Test
    fun `finished match does not accept points`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 1)

        controller.winSetForPlayerStartingOnLhs()

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
        controller.winGames(lhs, lhs, rhs, rhs, lhs, lhs, rhs, rhs, lhs, rhs)

        // set 2
        controller.simulateTieBreak()
        controller.scorePointsForRhs(numOfPoints = 6)
        controller.scorePointsForLhs(numOfPoints = 1)

        // set 3
        controller.winGames(lhs, lhs, rhs, rhs, rhs, lhs, lhs, rhs)

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

    @Test
    fun `switches sides after every odd game`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        assertPlayer1OnLhs(controller)

        controller.winGameForLhs()
        assertPlayer2OnLhs(controller)

        controller.winGameForRhs()
        assertPlayer2OnLhs(controller)

        controller.winGameForLhs()
        assertPlayer1OnLhs(controller)

        controller.winGameForRhs()
        assertPlayer1OnLhs(controller)

        controller.winGames(rhs, lhs)
        assertPlayer2OnLhs(controller)

        controller.winGames(lhs, rhs)
        assertPlayer1OnLhs(controller)
    }

    @Test
    fun `switches sides at the end of set if number of games is odd`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)
        // P1:0 - P2:0
        controller.winGames(rhs, rhs, rhs, lhs, lhs, rhs)
        // P2:1 - P1:5 (switched sides 3 times)
        assertPlayer2OnLhs(controller)
        controller.winGameForRhs()
        // P1:6 - P2:1 (switched)
        assertPlayer1OnLhs(controller)
        // set
        // P1:0 - P2:0
        controller.winGameForLhs()
        // P2:0 - P1:1 (switched)
        assertPlayer2OnLhs(controller)
    }

    @Test
    fun `does not switch sides at the end of set if number of games is even`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        // P1:0 - P2:0
        controller.winGames(lhs, rhs, rhs, lhs, lhs)
        // P2:0 - P1:5 (switched sides 2 times)
        assertPlayer2OnLhs(controller)
        controller.winGameForRhs() // P2:0 - P1:6
        assertPlayer2OnLhs(controller)
        // set
        // P2:0 - P1:0
        controller.winGameForLhs()
        // P1:0 - P2:1 (switched)
        assertPlayer1OnLhs(controller)
    }

    @Test
    fun `switches sides after every 6th point in a tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        controller.simulateTieBreak()

        assertPlayer1OnLhs(controller)
        controller.scorePointsForLhs(numOfPoints = 1)
        assertPlayer1OnLhs(controller)
        controller.scorePointsForRhs(numOfPoints = 1)
        assertPlayer1OnLhs(controller)
        controller.winPoints(lhs, lhs, rhs, rhs)
        assertPlayer2OnLhs(controller)
        repeat(3) { controller.winPoints(lhs, rhs) }
        assertPlayer1OnLhs(controller)
        repeat(3) { controller.winPoints(lhs, rhs) }
        assertPlayer2OnLhs(controller)
    }

    @Test
    fun `switches sides after tiebreak`() {
        val controller = MatchControllerImpl(numOfSetsToWin = 10)

        controller.simulateTieBreak()

        assertPlayer1OnLhs(controller)
        controller.scorePointsForLhs(numOfPoints = 6)
        assertPlayer2OnLhs(controller)
        controller.scorePointsForRhs(numOfPoints = 1)
        assertPlayer1OnLhs(controller)
    }
}
