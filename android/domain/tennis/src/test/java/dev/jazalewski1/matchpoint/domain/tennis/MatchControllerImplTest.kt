package dev.jazalewski1.matchpoint.domain.tennis

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MatchControllerImplTest {
    private fun assertLhsPointScored(outcome: PointOutcome) {
        assertThat(outcome).isEqualTo(PointOutcome.PointScored(Side.LHS))
    }

    private fun assertRhsPointScored(outcome: PointOutcome) {
        assertThat(outcome).isEqualTo(PointOutcome.PointScored(Side.RHS))
    }

    private fun assertLhsGameWon(outcome: PointOutcome) {
        assertThat(outcome).isEqualTo(PointOutcome.GameWon(Side.LHS))
    }

    private fun assertRhsGameWon(outcome: PointOutcome) {
        assertThat(outcome).isEqualTo(PointOutcome.GameWon(Side.RHS))
    }

    @Test
    fun `when initialized then has love all`() {
        val controller = MatchControllerImpl()

        val expected = MatchState(game = GameState.Ongoing(Points.LOVE, Points.LOVE))
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when lhs scores then points update`() {
        val controller = MatchControllerImpl()

        fun assertLhsPoints(expectedPoints: Points) {
            assertThat(controller.getState())
                .isEqualTo(MatchState(game = GameState.Ongoing(expectedPoints, Points.LOVE)))
        }

        assertLhsPointScored(controller.addLhsScore())
        assertLhsPoints(Points.FIFTEEN)
        assertLhsPointScored(controller.addLhsScore())
        assertLhsPoints(Points.THIRTY)
        assertLhsPointScored(controller.addLhsScore())
        assertLhsPoints(Points.FORTY)
    }

    @Test
    fun `when rhs scores then points update`() {
        val controller = MatchControllerImpl()

        fun assertRhsPoints(expectedPoints: Points) {
            assertThat(controller.getState())
                .isEqualTo(MatchState(game = GameState.Ongoing(Points.LOVE, expectedPoints)))
        }

        assertRhsPointScored(controller.addRhsScore())
        assertRhsPoints(Points.FIFTEEN)
        assertRhsPointScored(controller.addRhsScore())
        assertRhsPoints(Points.THIRTY)
        assertRhsPointScored(controller.addRhsScore())
        assertRhsPoints(Points.FORTY)
    }

    @Test
    fun `when lhs is forty first and scores then lhs wins`() {
        val controller = MatchControllerImpl()

        repeat(2) { controller.addRhsScore() }
        repeat(3) { controller.addLhsScore() }
        assertLhsGameWon(controller.addLhsScore())

        val expected = MatchState(game = GameState.Ongoing(Points.FORTY, Points.THIRTY))
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when rhs is forty first and scores then rhs wins`() {
        val controller = MatchControllerImpl()

        repeat(2) { controller.addLhsScore() }
        repeat(3) { controller.addRhsScore() }
        assertRhsGameWon(controller.addRhsScore())

        val expected = MatchState(game = GameState.Ongoing(Points.THIRTY, Points.FORTY))
        assertThat(controller.getState()).isEqualTo(expected)
    }

    private fun advanceToDeuce(controller: MatchControllerImpl) {
        repeat(3) { controller.addLhsScore() }
        repeat(3) { controller.addRhsScore() }
    }

    @Test
    fun `when both have forty then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)

        val expected = MatchState(game = GameState.Deuce)
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when deuce and lhs scores then it is advantage for lhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertLhsPointScored(controller.addLhsScore())

        val expected = MatchState(game = GameState.Advantage.Lhs)
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when deuce and rhs scores then it is advantage for rhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertRhsPointScored(controller.addRhsScore())

        val expected = MatchState(game = GameState.Advantage.Rhs)
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for lhs and lhs scores then lhs wins`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addLhsScore()
        assertLhsGameWon(controller.addLhsScore())

        val expected = MatchState(game = GameState.Advantage.Lhs)
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for rhs and rhs scores then rhs wins`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addRhsScore()
        assertRhsGameWon(controller.addRhsScore())

        val expected = MatchState(game = GameState.Advantage.Rhs)
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for lhs and rhs scores then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addLhsScore()
        assertRhsPointScored(controller.addRhsScore())

        val expected = MatchState(game = GameState.Deuce)
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `when advantage for rhs and lhs scores then it is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        controller.addRhsScore()
        assertLhsPointScored(controller.addLhsScore())

        val expected = MatchState(game = GameState.Deuce)
        assertThat(controller.getState()).isEqualTo(expected)
    }
}
