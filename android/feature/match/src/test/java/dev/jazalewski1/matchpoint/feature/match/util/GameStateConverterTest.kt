package dev.jazalewski1.matchpoint.feature.match.util

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.Points
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameStateConverterTest {
    @Test
    fun `converts lhs in regular game`() {
        fun assertLhsConverted(points: Points, expected: String) {
            val actual = GameState.Regular.Main(points, Points.LOVE).lhsToString()
            assertThat(actual).isEqualTo(expected)
        }
        assertLhsConverted(Points.LOVE, "0")
        assertLhsConverted(Points.FIFTEEN, "15")
        assertLhsConverted(Points.THIRTY, "30")
        assertLhsConverted(Points.FORTY, "40")
    }

    @Test
    fun `converts rhs in regular game`() {
        fun assertRhsConverted(points: Points, expected: String) {
            val actual = GameState.Regular.Main(Points.LOVE, points).rhsToString()
            assertThat(actual).isEqualTo(expected)
        }
        assertRhsConverted(Points.LOVE, "0")
        assertRhsConverted(Points.FIFTEEN, "15")
        assertRhsConverted(Points.THIRTY, "30")
        assertRhsConverted(Points.FORTY, "40")
    }

    @Test
    fun `converts deuce`() {
        val game = GameState.Regular.Deuce
        assertThat(game.lhsToString()).isEqualTo("40")
        assertThat(game.rhsToString()).isEqualTo("40")
    }

    @Test
    fun `converts lhs advantage`() {
        val game = GameState.Regular.Advantage.Lhs
        assertThat(game.lhsToString()).isEqualTo("AD")
        assertThat(game.rhsToString()).isEqualTo("40")
    }

    @Test
    fun `converts rhs advantage`() {
        val game = GameState.Regular.Advantage.Rhs
        assertThat(game.lhsToString()).isEqualTo("40")
        assertThat(game.rhsToString()).isEqualTo("AD")
    }

    @Test
    fun `converts lhs in tiebreak`() {
        assertThat(GameState.TieBreak(lhs = 0, rhs = 0).lhsToString()).isEqualTo("0")
        assertThat(GameState.TieBreak(lhs = 1, rhs = 0).lhsToString()).isEqualTo("1")
        assertThat(GameState.TieBreak(lhs = 3, rhs = 0).lhsToString()).isEqualTo("3")
        assertThat(GameState.TieBreak(lhs = 12, rhs = 0).lhsToString()).isEqualTo("12")
    }

    @Test
    fun `converts rhs in tiebreak`() {
        assertThat(GameState.TieBreak(lhs = 0, rhs = 0).rhsToString()).isEqualTo("0")
        assertThat(GameState.TieBreak(lhs = 0, rhs = 1).rhsToString()).isEqualTo("1")
        assertThat(GameState.TieBreak(lhs = 0, rhs = 3).rhsToString()).isEqualTo("3")
        assertThat(GameState.TieBreak(lhs = 0, rhs = 12).rhsToString()).isEqualTo("12")
    }
}
