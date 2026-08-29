package dev.jazalewski1.matchpoint.domain.tennis.controllers

import dev.jazalewski1.matchpoint.domain.tennis.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SetTest {
    private fun Set.assertGames(p1: Int, p2: Int) {
        assertThat(player1Games).isEqualTo(p1)
        assertThat(player2Games).isEqualTo(p2)
    }

    private fun Set.addToP1AndAssertNoneOutcome() {
        assertThat(addGame(winner = Player.ONE)).isEqualTo(SetOutcome.None)
    }

    private fun Set.addToP2AndAssertNoneOutcome() {
        assertThat(addGame(winner = Player.TWO)).isEqualTo(SetOutcome.None)
    }

    private fun Set.addToP1AndAssertFinishedOutcome() {
        assertThat(addGame(winner = Player.ONE)).isEqualTo(SetOutcome.Finished(Player.ONE))
    }

    private fun Set.addToP2AndAssertFinishedOutcome() {
        assertThat(addGame(winner = Player.TWO)).isEqualTo(SetOutcome.Finished(Player.TWO))
    }

    private fun Set.addToP1AndAssertTieBreakOutcome() {
        assertThat(addGame(winner = Player.ONE)).isEqualTo(SetOutcome.Tiebreak)
    }

    private fun Set.addToP2AndAssertTieBreakOutcome() {
        assertThat(addGame(winner = Player.TWO)).isEqualTo(SetOutcome.Tiebreak)
    }

    @Test
    fun `starts with 0 to 0`() {
        val set = Set()
        set.assertGames(0, 0)
    }

    @Test
    fun `adding games`() {
        fun test(numOfGamesP1: Int, numOfGamesP2: Int) {
            val set = Set()
            repeat(numOfGamesP1) {
                set.addToP1AndAssertNoneOutcome()
            }
            repeat(numOfGamesP2) {
                set.addToP2AndAssertNoneOutcome()
            }
            set.assertGames(numOfGamesP1, numOfGamesP2)
        }

        (1..5).forEach { numOfGamesP1 ->
            (1..5).forEach { numOfGamesP2 ->
                test(numOfGamesP1, numOfGamesP2)
            }
        }
    }

    @Test
    fun `finishes when player1 leads 6 to 4`() {
        val set = Set()
        repeat(4) {
            set.addToP2AndAssertNoneOutcome()
        }
        repeat(5) {
            set.addToP1AndAssertNoneOutcome()
        }
        set.addToP1AndAssertFinishedOutcome()
        set.assertGames(6, 4)
    }

    @Test
    fun `finishes when player2 leads 6 to 4`() {
        val set = Set()
        repeat(4) {
            set.addToP1AndAssertNoneOutcome()
        }
        repeat(5) {
            set.addToP2AndAssertNoneOutcome()
        }
        set.addToP2AndAssertFinishedOutcome()
        set.assertGames(4, 6)
    }

    @Test
    fun `does not finish when player1 leads 6 to 5`() {
        val set = Set()
        repeat(5) {
            set.addToP2AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP1AndAssertNoneOutcome()
        }
        set.assertGames(6, 5)
    }

    @Test
    fun `does not finish when player2 leads 6 to 5`() {
        val set = Set()
        repeat(5) {
            set.addToP1AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP2AndAssertNoneOutcome()
        }
        set.assertGames(5, 6)
    }

    @Test
    fun `finishes when player1 leads 7 to 5`() {
        val set = Set()
        repeat(5) {
            set.addToP2AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP1AndAssertNoneOutcome()
        }
        set.addToP1AndAssertFinishedOutcome()
        set.assertGames(7, 5)
    }

    @Test
    fun `finishes when player2 leads 7 to 5`() {
        val set = Set()
        repeat(5) {
            set.addToP1AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP2AndAssertNoneOutcome()
        }
        set.addToP2AndAssertFinishedOutcome()
        set.assertGames(5, 7)
    }

    @Test
    fun `adding point to player1 when it is 5 to 6 leads to tiebreak`() {
        val set = Set()
        repeat(5) {
            set.addToP1AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP2AndAssertNoneOutcome()
        }
        set.addToP1AndAssertTieBreakOutcome()
        set.assertGames(6, 6)
    }

    @Test
    fun `adding point to player2 when it is 5 to 6 leads to tiebreak`() {
        val set = Set()
        repeat(5) {
            set.addToP2AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP1AndAssertNoneOutcome()
        }
        set.addToP2AndAssertTieBreakOutcome()
        set.assertGames(6, 6)
    }

    @Test
    fun `adding point to player1 during tiebreak leads to win`() {
        val set = Set()
        repeat(5) {
            set.addToP1AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP2AndAssertNoneOutcome()
        }
        set.addToP1AndAssertTieBreakOutcome()
        set.addToP1AndAssertFinishedOutcome()
    }

    @Test
    fun `adding point to player2 during tiebreak leads to win`() {
        val set = Set()
        repeat(5) {
            set.addToP2AndAssertNoneOutcome()
        }
        repeat(6) {
            set.addToP1AndAssertNoneOutcome()
        }
        set.addToP2AndAssertTieBreakOutcome()
        set.addToP2AndAssertFinishedOutcome()
    }
}
