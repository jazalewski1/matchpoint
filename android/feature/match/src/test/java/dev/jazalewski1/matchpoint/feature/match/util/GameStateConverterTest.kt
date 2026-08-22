package dev.jazalewski1.matchpoint.feature.match.util

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.Points
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameStateConverterTest {
    @Test
    fun `converts lhs in ongoing game`() {
        fun assertLhsConverted(points: Points, expected: String) {
            val actual = GameState.Ongoing(points, Points.LOVE).lhsToString()
            assertThat(actual).isEqualTo(expected)
        }
        assertLhsConverted(Points.LOVE, "0")
        assertLhsConverted(Points.FIFTEEN, "15")
        assertLhsConverted(Points.THIRTY, "30")
        assertLhsConverted(Points.FORTY, "40")
    }

    @Test
    fun `converts rhs in ongoing game`() {
        fun assertRhsConverted(points: Points, expected: String) {
            val actual = GameState.Ongoing(Points.LOVE, points).rhsToString()
            assertThat(actual).isEqualTo(expected)
        }
        assertRhsConverted(Points.LOVE, "0")
        assertRhsConverted(Points.FIFTEEN, "15")
        assertRhsConverted(Points.THIRTY, "30")
        assertRhsConverted(Points.FORTY, "40")
    }

    @Test
    fun `converts deuce`() {
        val game = GameState.Deuce
        assertThat(game.lhsToString()).isEqualTo("40")
        assertThat(game.rhsToString()).isEqualTo("40")
    }

    @Test
    fun `converts lhs advantage`() {
        val game = GameState.Advantage.Lhs
        assertThat(game.lhsToString()).isEqualTo("AD")
        assertThat(game.rhsToString()).isEqualTo("40")
    }

    @Test
    fun `converts rhs advantage`() {
        val game = GameState.Advantage.Rhs
        assertThat(game.lhsToString()).isEqualTo("40")
        assertThat(game.rhsToString()).isEqualTo("AD")
    }
}
