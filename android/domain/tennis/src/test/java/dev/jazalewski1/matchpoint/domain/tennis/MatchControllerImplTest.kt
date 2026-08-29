package dev.jazalewski1.matchpoint.domain.tennis

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

private val sampleTotalMatchState =
    TotalMatchState(
        game = GameState.default(),
        set = SetState.default(),
        match = MatchState.default(),
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

    private fun assertLhsMatchWon(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.MatchWon)
    }

    private fun assertRhsMatchWon(event: MatchEvent) {
        assertThat(event).isEqualTo(MatchEvent.MatchWon)
    }

    @Test
    fun `has love all at beginning`() {
        val controller = MatchControllerImpl()

        val expected = sampleTotalMatchState
        assertThat(controller.getState()).isEqualTo(expected)
    }

    @Test
    fun `lhs point scored`() {
        val controller = MatchControllerImpl()

        assertLhsPointScored(controller.addPointToLhs())
        assertThat(controller.getState().game)
            .isEqualTo(GameState.Regular.Main(Points.FIFTEEN, Points.LOVE))
    }

    @Test
    fun `rhs point scored`() {
        val controller = MatchControllerImpl()

        assertRhsPointScored(controller.addPointToRhs())
        assertThat(controller.getState().game)
            .isEqualTo(GameState.Regular.Main(Points.LOVE, Points.FIFTEEN))
    }

    @Test
    fun `lhs wins game`() {
        val controller = MatchControllerImpl()

        repeat(2) { controller.addPointToRhs() }
        repeat(3) { controller.addPointToLhs() }
        assertLhsGameWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(sampleTotalMatchState.copy(set = SetState(lhsGames = 1, rhsGames = 0)))
    }

    @Test
    fun `rhs wins game`() {
        val controller = MatchControllerImpl()

        repeat(2) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
        assertRhsGameWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(sampleTotalMatchState.copy(set = SetState(lhsGames = 0, rhsGames = 1)))
    }

    private fun advanceToDeuce(controller: MatchControllerImpl) {
        repeat(3) { controller.addPointToLhs() }
        repeat(3) { controller.addPointToRhs() }
    }

    @Test
    fun `game is deuce`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)

        assertThat(controller.getState().game).isEqualTo(GameState.Regular.Deuce)
    }

    @Test
    fun `advantage for lhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertLhsPointScored(controller.addPointToLhs())

        val expected = GameState.Regular.Advantage.Lhs
        assertThat(controller.getState().game).isEqualTo(expected)
    }

    @Test
    fun `advantage for rhs`() {
        val controller = MatchControllerImpl()

        advanceToDeuce(controller)
        assertRhsPointScored(controller.addPointToRhs())

        val expected = GameState.Regular.Advantage.Rhs
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
    fun `lhs wins set`() {
        val controller = MatchControllerImpl()

        repeat(4) { winGameForRhs(controller) }
        repeat(5) { winGameForLhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsSetWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(sampleTotalMatchState.copy(match = MatchState(lhsSets = 1, rhsSets = 0)))
    }

    @Test
    fun `rhs wins set`() {
        val controller = MatchControllerImpl()

        repeat(4) { winGameForLhs(controller) }
        repeat(5) { winGameForRhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsSetWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(sampleTotalMatchState.copy(match = MatchState(lhsSets = 0, rhsSets = 1)))
    }

    @Test
    fun `lhs ties to tiebreak`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForLhs(controller) }
        repeat(6) { winGameForRhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)
        assertLhsGameWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(
                sampleTotalMatchState.copy(
                    game = GameState.TieBreak(lhsPoints = 0, rhsPoints = 0),
                    set = SetState(lhsGames = 6, rhsGames = 6),
                )
            )
    }

    @Test
    fun `rhs ties to tiebreak`() {
        val controller = MatchControllerImpl()

        repeat(5) { winGameForRhs(controller) }
        repeat(6) { winGameForLhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)
        assertRhsGameWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(
                sampleTotalMatchState.copy(
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
    fun `lhs wins tiebreak`() {
        val controller = MatchControllerImpl()

        advanceToTieBreak(controller)
        scorePointsForRhs(controller, numOfPoints = 5)
        scorePointsForLhs(controller, numOfPoints = 6)

        assertLhsSetWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(sampleTotalMatchState.copy(match = MatchState(lhsSets = 1, rhsSets = 0)))
    }

    @Test
    fun `rhs wins tiebreak`() {
        val controller = MatchControllerImpl()

        advanceToTieBreak(controller)
        scorePointsForLhs(controller, numOfPoints = 5)
        scorePointsForRhs(controller, numOfPoints = 6)

        assertRhsSetWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(sampleTotalMatchState.copy(match = MatchState(lhsSets = 0, rhsSets = 1)))
    }

    private fun winSetForLhs(controller: MatchController) {
        repeat(6) {
            winGameForLhs(controller)
        }
    }

    private fun winSetForRhs(controller: MatchController) {
        repeat(6) {
            winGameForRhs(controller)
        }
    }

    @Test
    fun `lhs wins match`() {
        val controller = MatchControllerImpl()

        repeat(1) { winSetForRhs(controller) }
        repeat(2) { winSetForLhs(controller) }

        repeat(5) { winGameForLhs(controller) }
        scorePointsForLhs(controller, numOfPoints = 3)

        assertLhsMatchWon(controller.addPointToLhs())

        assertThat(controller.getState())
            .isEqualTo(
                TotalMatchState(
                    game =
                        GameState.Regular.Main(lhsPoints = Points.FORTY, rhsPoints = Points.LOVE),
                    set = SetState(lhsGames = 6, rhsGames = 0),
                    match = MatchState(lhsSets = 3, rhsSets = 1),
                )
            )
    }

    @Test
    fun `rhs wins match`() {
        val controller = MatchControllerImpl()

        repeat(1) { winSetForLhs(controller) }
        repeat(2) { winSetForRhs(controller) }

        repeat(5) { winGameForRhs(controller) }
        scorePointsForRhs(controller, numOfPoints = 3)

        assertRhsMatchWon(controller.addPointToRhs())

        assertThat(controller.getState())
            .isEqualTo(
                TotalMatchState(
                    game =
                        GameState.Regular.Main(lhsPoints = Points.LOVE, rhsPoints = Points.FORTY),
                    set = SetState(lhsGames = 0, rhsGames = 6),
                    match = MatchState(lhsSets = 1, rhsSets = 3),
                )
            )
    }
}
