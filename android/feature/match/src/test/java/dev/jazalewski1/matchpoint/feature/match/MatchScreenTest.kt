package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val LHS_PLAYER_NAME = "Left player"
private const val RHS_PLAYER_NAME = "Right player"
private const val LHS_SCORE = "40"
private const val RHS_SCORE = "15"

@RunWith(AndroidJUnit4::class)
class MatchScreenTest {
    @get:Rule val rule = createComposeRule()

    @Composable
    private fun SutScreen(
        lhsPlayerName: String = LHS_PLAYER_NAME,
        rhsPlayerName: String = RHS_PLAYER_NAME,
        lhsScore: String = LHS_SCORE,
        rhsScore: String = RHS_SCORE,
        onLhsClick: () -> Unit = {},
        onRhsClick: () -> Unit = {},
        lhsIndication: Indication? = null,
        rhsIndication: Indication? = null,
    ) =
        MatchScreen(
            lhsPlayerName = lhsPlayerName,
            rhsPlayerName = rhsPlayerName,
            lhsScore = lhsScore,
            rhsScore = rhsScore,
            onLhsClick = onLhsClick,
            onRhsClick = onRhsClick,
            lhsIndication = lhsIndication,
            rhsIndication = rhsIndication,
        )

    @Test
    fun `displays initial points`() {
        rule.setContent { SutScreen() }

        rule
            .onNodeWithContentDescription("Left Score")
            .assertIsDisplayed()
            .assert(hasText(LHS_SCORE))
            .assertHasClickAction()

        rule
            .onNodeWithContentDescription("Right Score")
            .assertIsDisplayed()
            .assert(hasText(RHS_SCORE))
            .assertHasClickAction()
    }

    @Test
    fun `displays player names`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText(LHS_PLAYER_NAME).assertIsDisplayed()

        rule.onNodeWithText(RHS_PLAYER_NAME).assertIsDisplayed()
    }

    @Test
    fun `when lhs point is clicked then it is increased`() {
        var triggered = false
        rule.setContent { SutScreen(onLhsClick = { triggered = true }) }

        rule.onNodeWithContentDescription("Left Score").performClick()
        assertTrue(triggered)
    }

    @Test
    fun `when rhs point is clicked then it is increased`() {
        var triggered = false
        rule.setContent { SutScreen(onRhsClick = { triggered = true }) }

        rule.onNodeWithContentDescription("Right Score").performClick()
        assertTrue(triggered)
    }
}
