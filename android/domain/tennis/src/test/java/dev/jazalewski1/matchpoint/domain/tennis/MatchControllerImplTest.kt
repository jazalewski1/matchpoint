package dev.jazalewski1.matchpoint.domain.tennis

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

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

        val expected = GameState.default()
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when lhs scores then points update`() {
        val controller = MatchControllerImpl()

        fun assertLhsPoints(expectedPoints: Points) {
            assertThat(controller.getCurrentGame())
                .isEqualTo(GameState.Regular(expectedPoints, Points.LOVE))
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
            assertThat(controller.getCurrentGame())
                .isEqualTo(GameState.Regular(Points.LOVE, expectedPoints))
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

        val expected = GameState.default()
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when rhs is forty first and scores then rhs wins`() {
        val controller = MatchControllerImpl()

        repeat(2) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
        assertRhsGameWon(controller.addPointToRhs())

        val expected = GameState.default()
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    private fun advanceToDeuce(controller: MatchControllerImpl) {
        repeat(3) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
    }

    @Test
    fun `when both have forty then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)

        val expected = GameState.Deuce
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when deuce and lhs scores then it is advantage for lhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertLhsPointScored(controller.addPointToLhs())

        val expected = GameState.Advantage.Lhs
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when deuce and rhs scores then it is advantage for rhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertRhsPointScored(controller.addPointToRhs())

        val expected = GameState.Advantage.Rhs
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for lhs and lhs scores then lhs wins`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToLhs()
        assertLhsGameWon(controller.addPointToLhs())

        val expected = GameState.default()
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for rhs and rhs scores then rhs wins`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToRhs()
        assertRhsGameWon(controller.addPointToRhs())

        val expected = GameState.default()
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for lhs and rhs scores then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToLhs()
        assertRhsPointScored(controller.addPointToRhs())

        val expected = GameState.Deuce
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for rhs and lhs scores then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addPointToRhs()
        assertLhsPointScored(controller.addPointToLhs())

        val expected = GameState.Deuce
        assertThat(controller.getCurrentGame()).isEqualTo(expected)
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
    fun `when lhs wins 6 games first then lhs wins set`() {
        (0..4).forEach { rhsGames ->
            val controller = MatchControllerImpl()

            repeat(rhsGames) { winGameForRhs(controller) }
            repeat(5) { winGameForLhs(controller) }
            scorePointsForLhs(controller, numOfPoints = 3)
            assertLhsSetWon(controller.addPointToLhs())
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
        }
    }

    @Test
    fun `when lhs wins 6 games over 5 then lhs does not win set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForRhs(controller) }
        repeat(5) { winGameForLhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsGameWon(controller.addPointToLhs())
    }

    @Test
    fun `when rhs wins 6 games over 5 then rhs does not win set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForLhs(controller) }
        repeat(5) { winGameForRhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsGameWon(controller.addPointToRhs())
    }

    @Test
    fun `when lhs wins 7 games over 5 then lhs wins set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForRhs(controller) }
        repeat(6) { winGameForLhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsSetWon(controller.addPointToLhs())
    }

    @Test
    fun `when rhs wins 7 games over 5 then rhs wins set`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForLhs(controller) }
        repeat(6) { winGameForRhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsSetWon(controller.addPointToRhs())
    }

    /*
    @Test
    fun `when lhs wins 6 games and ties to 6 then it is tiebreak`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForLhs(controller) }
        repeat(6) { winGameForRhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsGameWon(controller.addPointToLhs())
        TODO("test current game for tiebreak")
    }

    @Test
    fun `when rhs wins 6 games and ties to 6 then it is tiebreak`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForRhs(controller) }
        repeat(6) { winGameForLhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsGameWon(controller.addPointToRhs())
        TODO("test current game for tiebreak")
    }

    @Test
    fun `more tiebreak tests`() {
        TODO()
    }
    */
}
