package dev.jazalewski1.matchpoint.domain.tennis.controllers

import dev.jazalewski1.matchpoint.domain.tennis.details.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MatchTest {
    private fun Match.addToP1AndAssertNoneOutcome() {
        val outcome = addSet(winner = Player.ONE)
        assertThat(outcome).isEqualTo(MatchOutcome.None)
    }

    private fun Match.addToP2AndAssertNoneOutcome() {
        val outcome = addSet(winner = Player.TWO)
        assertThat(outcome).isEqualTo(MatchOutcome.None)
    }

    private fun Match.addToP1AndAssertFinishedOutcome() {
        val outcome = addSet(winner = Player.ONE)
        assertThat(outcome).isEqualTo(MatchOutcome.Finished(winner = Player.ONE))
    }

    private fun Match.addToP2AndAssertFinishedOutcome() {
        val outcome = addSet(winner = Player.TWO)
        assertThat(outcome).isEqualTo(MatchOutcome.Finished(winner = Player.TWO))
    }

    @Test
    fun `starts with 0 to 0`() {
        val match = Match(numOfSetsToWin = 3)
        assertThat(match.player1Sets).isEqualTo(0)
        assertThat(match.player2Sets).isEqualTo(0)
    }

    @Test
    fun `adding sets`() {
        val match = Match(numOfSetsToWin = 10)

        val numOfSetsP1 = 4
        repeat(numOfSetsP1) {
            match.addToP1AndAssertNoneOutcome()
        }
        val numOfSetsP2 = 3
        repeat(numOfSetsP2) {
            match.addToP2AndAssertNoneOutcome()
        }

        assertThat(match.player1Sets).isEqualTo(numOfSetsP1)
        assertThat(match.player2Sets).isEqualTo(numOfSetsP2)
    }

    @Test
    fun `finishes when player 1 wins required number of sets`() {
        val match = Match(numOfSetsToWin = 3)

        repeat(2) {
            match.addToP2AndAssertNoneOutcome()
            match.addToP1AndAssertNoneOutcome()
        }
        match.addToP1AndAssertFinishedOutcome()

        assertThat(match.player1Sets).isEqualTo(3)
        assertThat(match.player2Sets).isEqualTo(2)
    }

    @Test
    fun `finishes when player 2 wins required number of sets`() {
        val match = Match(numOfSetsToWin = 3)

        repeat(2) {
            match.addToP1AndAssertNoneOutcome()
            match.addToP2AndAssertNoneOutcome()
        }
        match.addToP2AndAssertFinishedOutcome()

        assertThat(match.player1Sets).isEqualTo(2)
        assertThat(match.player2Sets).isEqualTo(3)
    }
}
