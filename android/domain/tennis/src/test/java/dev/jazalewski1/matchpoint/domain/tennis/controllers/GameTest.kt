package dev.jazalewski1.matchpoint.domain.tennis.controllers

import dev.jazalewski1.matchpoint.domain.tennis.Points
import dev.jazalewski1.matchpoint.domain.tennis.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameTest {
    private fun assertPointScoredByPlayer1(outcome: GameOutcome) {
        assertThat(outcome).isEqualTo(GameOutcome.PointScored(winner = Player.ONE))
    }

    private fun assertPointScoredByPlayer2(outcome: GameOutcome) {
        assertThat(outcome).isEqualTo(GameOutcome.PointScored(winner = Player.TWO))
    }

    private fun assertGameFinishedByPlayer1(outcome: GameOutcome) {
        assertThat(outcome).isEqualTo(GameOutcome.Finished(winner = Player.ONE))
    }

    private fun assertGameFinishedByPlayer2(outcome: GameOutcome) {
        assertThat(outcome).isEqualTo(GameOutcome.Finished(winner = Player.TWO))
    }

    private fun Game.addAndAssertPointScoredByPlayer1(numOfPoints: Int = 1) {
        repeat(numOfPoints) {
            assertPointScoredByPlayer1(addPoint(winner = Player.ONE))
        }
    }

    private fun Game.addAndAssertPointScoredByPlayer2(numOfPoints: Int = 1) {
        repeat(numOfPoints) {
            assertPointScoredByPlayer2(addPoint(winner = Player.TWO))
        }
    }

    @Test
    fun `starts regular game with love all`() {
        val game = RegularGame()
        assertThat(game.phase).isEqualTo(Phase.Main(player1 = Points.LOVE, player2 = Points.LOVE))
    }

    @Test
    fun `adding points during regular main game continues`() {
        fun test(numOfPointsP1: Int, numOfPointsP2: Int, expectedP1: Points, expectedP2: Points) {
            val game = RegularGame()
            game.addAndAssertPointScoredByPlayer1(numOfPointsP1)
            game.addAndAssertPointScoredByPlayer2(numOfPointsP2)
            assertThat(game.phase).isEqualTo(Phase.Main(player1 = expectedP1, player2 = expectedP2))
        }

        test(0, 1, Points.LOVE, Points.FIFTEEN)
        test(0, 2, Points.LOVE, Points.THIRTY)
        test(0, 3, Points.LOVE, Points.FORTY)

        test(1, 0, Points.FIFTEEN, Points.LOVE)
        test(1, 1, Points.FIFTEEN, Points.FIFTEEN)
        test(1, 2, Points.FIFTEEN, Points.THIRTY)
        test(1, 3, Points.FIFTEEN, Points.FORTY)

        test(2, 0, Points.THIRTY, Points.LOVE)
        test(2, 1, Points.THIRTY, Points.FIFTEEN)
        test(2, 2, Points.THIRTY, Points.THIRTY)
        test(2, 3, Points.THIRTY, Points.FORTY)

        test(3, 0, Points.FORTY, Points.LOVE)
        test(3, 1, Points.FORTY, Points.FIFTEEN)
        test(3, 2, Points.FORTY, Points.THIRTY)
    }

    @Test
    fun `adding point to player1 during regular main game leads to win`() {
        val game = RegularGame()
        game.addAndAssertPointScoredByPlayer2(2)
        game.addAndAssertPointScoredByPlayer1(3)
        assertGameFinishedByPlayer1(game.addPoint(Player.ONE))
        assertThat(game.phase)
            .isEqualTo(Phase.Main(player1 = Points.FORTY, player2 = Points.THIRTY))
    }

    @Test
    fun `adding point to player2 during regular main game leads to win`() {
        val game = RegularGame()
        game.addAndAssertPointScoredByPlayer1(2)
        game.addAndAssertPointScoredByPlayer2(3)
        assertGameFinishedByPlayer2(game.addPoint(Player.TWO))
        assertThat(game.phase)
            .isEqualTo(Phase.Main(player1 = Points.THIRTY, player2 = Points.FORTY))
    }

    private fun advanceToDeuce(game: Game) {
        game.addAndAssertPointScoredByPlayer1(3)
        game.addAndAssertPointScoredByPlayer2(3)
    }

    @Test
    fun `adding point to player1 during regular main game with 30 to 40 leads to deuce`() {
        val game = RegularGame()
        game.addAndAssertPointScoredByPlayer2(3)
        game.addAndAssertPointScoredByPlayer1(3)
        assertThat(game.phase).isEqualTo(Phase.Deuce)
    }

    @Test
    fun `adding point to player2 during regular main game with 30 to 40 leads to deuce`() {
        val game = RegularGame()
        game.addAndAssertPointScoredByPlayer1(3)
        game.addAndAssertPointScoredByPlayer2(3)
        assertThat(game.phase).isEqualTo(Phase.Deuce)
    }

    @Test
    fun `adding point to player1 during regular deuce game leads to advantage`() {
        val game = RegularGame()
        advanceToDeuce(game)
        assertPointScoredByPlayer1(game.addPoint(winner = Player.ONE))

        assertThat(game.phase).isEqualTo(Phase.Advantage(player = Player.ONE))
    }

    @Test
    fun `adding point to player2 during regular deuce game leads to advantage`() {
        val game = RegularGame()
        advanceToDeuce(game)
        assertPointScoredByPlayer2(game.addPoint(winner = Player.TWO))

        assertThat(game.phase).isEqualTo(Phase.Advantage(player = Player.TWO))
    }

    @Test
    fun `adding point to player1 during advantage for player2 leads to deuce`() {
        val game = RegularGame()
        advanceToDeuce(game)
        assertPointScoredByPlayer1(game.addPoint(winner = Player.ONE))
        assertPointScoredByPlayer2(game.addPoint(winner = Player.TWO))

        assertThat(game.phase).isEqualTo(Phase.Deuce)
    }

    @Test
    fun `adding point to player2 during advantage for player1 leads to deuce`() {
        val game = RegularGame()
        advanceToDeuce(game)
        assertPointScoredByPlayer2(game.addPoint(winner = Player.TWO))
        assertPointScoredByPlayer1(game.addPoint(winner = Player.ONE))

        assertThat(game.phase).isEqualTo(Phase.Deuce)
    }

    @Test
    fun `adding point to player1 during advantage for player1 leads to win`() {
        val game = RegularGame()
        advanceToDeuce(game)
        assertPointScoredByPlayer1(game.addPoint(winner = Player.ONE))
        assertGameFinishedByPlayer1(game.addPoint(winner = Player.ONE))
        assertThat(game.phase).isEqualTo(Phase.Advantage(player = Player.ONE))
    }

    @Test
    fun `adding point to player2 during advantage for player2 leads to win`() {
        val game = RegularGame()
        advanceToDeuce(game)
        assertPointScoredByPlayer2(game.addPoint(winner = Player.TWO))
        assertGameFinishedByPlayer2(game.addPoint(winner = Player.TWO))
        assertThat(game.phase).isEqualTo(Phase.Advantage(player = Player.TWO))
    }

    @Test
    fun `starts tiebreak with 0 to 0`() {
        val game = TieBreakGame()
        assertThat(game.player1).isEqualTo(0)
        assertThat(game.player2).isEqualTo(0)
    }

    @Test
    fun `adding points during tiebreak`() {
        fun test(numOfPointsP1: Int, numOfPointsP2: Int) {
            val game = TieBreakGame()
            game.addAndAssertPointScoredByPlayer1(numOfPointsP1)
            game.addAndAssertPointScoredByPlayer2(numOfPointsP2)
            assertThat(game.player1).isEqualTo(numOfPointsP1)
            assertThat(game.player2).isEqualTo(numOfPointsP2)
        }
        repeat(7) { numOfPointsP1 ->
            repeat(7) { numOfPointsP2 ->
                test(numOfPointsP1, numOfPointsP2)
            }
        }
    }

    @Test
    fun `adding more than 7 points during tiebreak`() {
        val game = TieBreakGame()
        repeat(12) {
            game.addAndAssertPointScoredByPlayer1()
            game.addAndAssertPointScoredByPlayer2()
        }
        assertThat(game.player1).isEqualTo(12)
        assertThat(game.player2).isEqualTo(12)
    }

    @Test
    fun `adding points to player1 during tiebreak leading by 2 leads to win`() {
        fun test(numOfPointsP2: Int) {
            val game = TieBreakGame()
            repeat(numOfPointsP2) {
                game.addAndAssertPointScoredByPlayer1()
                game.addAndAssertPointScoredByPlayer2()
            }
            game.addAndAssertPointScoredByPlayer1()
            assertGameFinishedByPlayer1(game.addPoint(winner = Player.ONE))
            assertThat(game.player1).isEqualTo(numOfPointsP2 + 2)
            assertThat(game.player2).isEqualTo(numOfPointsP2)
        }
        test(numOfPointsP2 = 5)
        test(numOfPointsP2 = 7)
        test(numOfPointsP2 = 15)
    }

    @Test
    fun `adding points to player2 during tiebreak leading by 2 leads to win`() {
        fun test(numOfPointsP1: Int) {
            val game = TieBreakGame()
            repeat(numOfPointsP1) {
                game.addAndAssertPointScoredByPlayer2()
                game.addAndAssertPointScoredByPlayer1()
            }
            game.addAndAssertPointScoredByPlayer2()
            assertGameFinishedByPlayer2(game.addPoint(winner = Player.TWO))
            assertThat(game.player1).isEqualTo(numOfPointsP1)
            assertThat(game.player2).isEqualTo(numOfPointsP1 + 2)
        }
        test(numOfPointsP1 = 5)
        test(numOfPointsP1 = 7)
        test(numOfPointsP1 = 15)
    }
}
