package dev.jazalewski1.matchpoint.feature.match.util

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.Points
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameStateConverterTest {
    private fun assertConverted(game: GameState, expectedLhs: String, expectedRhs: String) {
        val actual = game.toPairOfStrings()
        assertThat(actual).isEqualTo(Pair(expectedLhs, expectedRhs))
    }

    @Test
    fun `converts lhs in ongoing game`() {
        fun assertLhsConverted(points: Points, expected: String) {
            val actual = GameState.Ongoing(points, Points.LOVE).toPairOfStrings()
            assertThat(actual.first).isEqualTo(expected)
        }
        assertLhsConverted(Points.LOVE, "0")
        assertLhsConverted(Points.FIFTEEN, "15")
        assertLhsConverted(Points.THIRTY, "30")
        assertLhsConverted(Points.FORTY, "40")
    }

    @Test
    fun `converts rhs in ongoing game`() {
        fun assertRhsConverted(points: Points, expected: String) {
            val actual = GameState.Ongoing(Points.LOVE, points).toPairOfStrings()
            assertThat(actual.second).isEqualTo(expected)
        }
        assertRhsConverted(Points.LOVE, "0")
        assertRhsConverted(Points.FIFTEEN, "15")
        assertRhsConverted(Points.THIRTY, "30")
        assertRhsConverted(Points.FORTY, "40")
    }

    @Test
    fun `converts deuce`() {
        assertConverted(GameState.Deuce, "40", "40")
    }

    @Test
    fun `converts advantages`() {
        assertConverted(GameState.Advantage.Lhs, "AD", "40")
        assertConverted(GameState.Advantage.Rhs, "40", "AD")
    }
}
