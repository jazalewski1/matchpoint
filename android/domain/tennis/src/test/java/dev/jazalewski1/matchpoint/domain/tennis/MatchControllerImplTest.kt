package dev.jazalewski1.matchpoint.domain.tennis

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

private val sampleMatchState =
    MatchState(
        game = GameState.default(),
        set = SetState.default(),
        lhsSets = 0,
        rhsSets = 0,
    )

class MatchControllerImplTest {
    private fun assertLhsPointScored(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.PointScored)
    }

    private fun assertRhsPointScored(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.PointScored)
    }

    private fun assertLhsGameWon(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.GameWon)
    }

    private fun assertRhsGameWon(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.GameWon)
    }

    private fun assertLhsSetWon(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.SetWon)
    }

    private fun assertRhsSetWon(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.SetWon)
    }

    @Test
    fun `when initialized then has love all`() {
        val controller = MatchControllerImpl()

        val expected = sampleMatchState
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when lhs scores then points update`() {
        val controller = MatchControllerImpl()

        fun assertLhsPoints(expectedPoints: Points) {
            assertThat(controller.getState().game)
                .isEqualTo(GameState.Regular.Main(expectedPoints, Points.LOVE))
        }

        assertLhsPointScored(controller.addPointToLhs())
        assertLhsPoints(Points.FIFTEEN)
        assertLhsPointScored(controller.addPointToLhs())
        assertLhsPoints(Points.THIRTY)
        assertLhsPointScored(controller.addPointToLhs())
        assertLhsPoints(Points.FORTY)
    }

    @Test
    fun `when rhs scores then points update`() {
        val controller = MatchControllerImpl()

        fun assertRhsPoints(expectedPoints: Points) {
            assertThat(controller.getState().game)
                .isEqualTo(GameState.Regular.Main(Points.LOVE, expectedPoints))
        }

        assertRhsPointScored(controller.addPointToRhs())
        assertRhsPoints(Points.FIFTEEN)
        assertRhsPointScored(controller.addPointToRhs())
        assertRhsPoints(Points.THIRTY)
        assertRhsPointScored(controller.addPointToRhs())
        assertRhsPoints(Points.FORTY)
    }

    @Test
    fun `when lhs is forty first and scores then lhs wins`() {
        val controller = MatchControllerImpl()

        repeat(2) { controller.addPointToRhs() }
        repeat(3) { controller.addPointToLhs() }
        assertLhsGameWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(sampleMatchState.copy(set = SetState(lhsGames = 1, rhsGames = 0)))
    }

    @Test
    fun `when rhs is forty first and scores then rhs wins`() {
        val controller = MatchControllerImpl()

        repeat(2) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
        assertRhsGameWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(sampleMatchState.copy(set = SetState(lhsGames = 0, rhsGames = 1)))
    }

    private fun advanceToDeuce(controller: MatchControllerImpl) {
        repeat(3) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
    }

    @Test
    fun `when both have forty then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)

        val expected = GameState.Regular.Deuce
        assertThat(controller.getState().game).isEqualTo(expected)
    }

    @Test
    fun `when deuce and lhs scores then it is advantage for lhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertLhsPointScored(controller.addPointToLhs())

        val expected = GameState.Regular.Advantage.Lhs
        assertThat(controller.getState().game).isEqualTo(expected)
    }

    @Test
    fun `when deuce and rhs scores then it is advantage for rhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertRhsPointScored(controller.addPointToRhs())

        val expected = GameState.Regular.Advantage.Rhs
        assertThat(controller.getState().game).isEqualTo(expected)
    }

    @Test
    fun `when advantage for lhs and lhs scores then lhs wins`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToLhs()
        assertLhsGameWon(controller.addPointToLhs())

        val expected = GameState.default()
        assertThat(controller.getState().game).isEqualTo(expected)
    }

    @Test
    fun `when advantage for rhs and rhs scores then rhs wins`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToRhs()
        assertRhsGameWon(controller.addPointToRhs())

        val expected = GameState.default()
        assertThat(controller.getState().game).isEqualTo(expected)
    }

    @Test
    fun `when advantage for lhs and rhs scores then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToLhs()
        assertRhsPointScored(controller.addPointToRhs())

        val expected = GameState.Regular.Deuce
        assertThat(controller.getState().game).isEqualTo(expected)
    }

    @Test
    fun `when advantage for rhs and lhs scores then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToRhs()
        assertLhsPointScored(controller.addPointToLhs())

        val expected = GameState.Regular.Deuce
        assertThat(controller.getState().game).isEqualTo(expected)
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
    fun `when players win games then set state is updated`() {
        val controller = MatchControllerImpl()

        fun assertGames(expectedLhs: Int, expectedRhs: Int) {
            assertThat(controller.getState().set)
                .isEqualTo(SetState(lhsGames = expectedLhs, rhsGames = expectedRhs))
        }

        repeat(3) { winGameForLhs(controller) }
        assertGames(expectedLhs = 3, expectedRhs = 0)

        winGameForRhs(controller)
        assertGames(expectedLhs = 3, expectedRhs = 1)

        winGameForLhs(controller)
        assertGames(expectedLhs = 4, expectedRhs = 1)

        repeat(3) { winGameForRhs(controller) }
        assertGames(expectedLhs = 4, expectedRhs = 4)

        winGameForLhs(controller)
        assertGames(expectedLhs = 5, expectedRhs = 4)
    }

    @Test
    fun `when lhs wins 6 games first then lhs wins set`() {
        (0..4).forEach { rhsGames ->
            val controller = MatchControllerImpl()

            repeat(rhsGames) { winGameForRhs(controller) }
            repeat(5) { winGameForLhs(controller) }
            scorePointsForLhs(controller, numOfPoints = 3)
            assertLhsSetWon(controller.addPointToLhs())

            assertThat(controller.getState()).isEqualTo(sampleMatchState.copy(lhsSets = 1))
        }
    }

    @Test
    fun `when rhs wins 6 games first then rhs wins set`() {
        (0..4).forEach { lhsGames ->
            val controller = MatchControllerImpl()

            repeat(lhsGames) { winGameForLhs(controller) }
            repeat(5) { winGameForRhs(controller) }
            scorePointsForRhs(controller, numOfPoints = 3)
            assertRhsSetWon(controller.addPointToRhs())

            assertThat(controller.getState()).isEqualTo(sampleMatchState.copy(rhsSets = 1))
        }
    }

    @Test
    fun `when lhs wins 6 games over 5 then lhs does not win set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForRhs(controller) }
        repeat(5) { winGameForLhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsGameWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(sampleMatchState.copy(set = SetState(lhsGames = 6, rhsGames = 5)))
    }

    @Test
    fun `when rhs wins 6 games over 5 then rhs does not win set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForLhs(controller) }
        repeat(5) { winGameForRhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsGameWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(sampleMatchState.copy(set = SetState(lhsGames = 5, rhsGames = 6)))
    }

    @Test
    fun `when lhs wins 7 games over 5 then lhs wins set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForRhs(controller) }
        repeat(6) { winGameForLhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsSetWon(controller.addPointToLhs())

        assertThat(controller.getState()).isEqualTo(sampleMatchState.copy(lhsSets = 1))
    }

    @Test
    fun `when rhs wins 7 games over 5 then rhs wins set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForLhs(controller) }
        repeat(6) { winGameForRhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsSetWon(controller.addPointToRhs())

        assertThat(controller.getState()).isEqualTo(sampleMatchState.copy(rhsSets = 1))
    }

    @Test
    fun `when lhs wins 6 games and ties to 6 then tiebreak starts`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForLhs(controller) }
        repeat(6) { winGameForRhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsGameWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(
                sampleMatchState.copy(
                    game = GameState.TieBreak(lhsPoints = 0, rhsPoints = 0),
                    set = SetState(lhsGames = 6, rhsGames = 6),
                )
            )
    }

    @Test
    fun `when rhs wins 6 games and ties to 6 then tiebreak starts`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForRhs(controller) }
        repeat(6) { winGameForLhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsGameWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(
                sampleMatchState.copy(
                    game = GameState.TieBreak(lhsPoints = 0, rhsPoints = 0),
                    set = SetState(lhsGames = 6, rhsGames = 6),
                )
            )
    }

    private fun advanceToTieBreak(controller: MatchController) {
        repeat(5) { winGameForRhs(controller) }
        repeat(6) { winGameForLhs(controller) }
        winGameForRhs(controller)
    }

    @Test
    fun `when players score during tiebreak then game state is updated`() {
        val controller = MatchControllerImpl()

        fun assertPoints(expectedLhs: Int, expectedRhs: Int) {
            assertThat(controller.getState().game)
                .isEqualTo(GameState.TieBreak(lhsPoints = expectedLhs, rhsPoints = expectedRhs))
        }

        advanceToTieBreak(controller)
        scorePointsForLhs(controller, numOfPoints = 3)
        assertPoints(expectedLhs = 3, expectedRhs = 0)

        scorePointsForRhs(controller, numOfPoints = 2)
        assertPoints(expectedLhs = 3, expectedRhs = 2)

        scorePointsForLhs(controller, numOfPoints = 1)
        assertPoints(expectedLhs = 4, expectedRhs = 2)

        scorePointsForRhs(controller, numOfPoints = 3)
        assertPoints(expectedLhs = 4, expectedRhs = 5)
    }

    @Test
    fun `when lhs scores at least 7 points with 2 difference then lhs wins tiebreak`() {
        val controller = MatchControllerImpl()

        advanceToTieBreak(controller)
        scorePointsForRhs(controller, numOfPoints = 5)
        scorePointsForLhs(controller, numOfPoints = 6)

        assertLhsSetWon(controller.addPointToLhs())

        assertThat(controller.getState()).isEqualTo(sampleMatchState.copy(lhsSets = 1))
    }

    @Test
    fun `when rhs scores at least 7 points with 2 difference then rhs wins tiebreak`() {
        val controller = MatchControllerImpl()

        advanceToTieBreak(controller)
        scorePointsForLhs(controller, numOfPoints = 5)
        scorePointsForRhs(controller, numOfPoints = 6)

        assertRhsSetWon(controller.addPointToRhs())

        assertThat(controller.getState()).isEqualTo(sampleMatchState.copy(rhsSets = 1))
    }
}
