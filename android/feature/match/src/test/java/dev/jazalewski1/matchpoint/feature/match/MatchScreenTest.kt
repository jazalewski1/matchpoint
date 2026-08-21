package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val LHS_PLAYER_NAME = "Left player"
private const val RHS_PLAYER_NAME = "Right player"

@RunWith(AndroidJUnit4::class)
class SutScreenTest {
    @get:Rule val rule = createComposeRule()

    @Composable
    private fun SutScreen(
        lhsPlayerName: String = LHS_PLAYER_NAME,
        rhsPlayerName: String = RHS_PLAYER_NAME,
    ) = MatchScreen(lhsPlayerName = lhsPlayerName, rhsPlayerName = rhsPlayerName)

    @Test
    fun `displays initial points`() {
        rule.setContent { SutScreen() }

        rule
            .onNodeWithContentDescription("Left Score")
            .assertIsDisplayed()
            .assert(hasText("0"))
            .assertHasClickAction()

        rule
            .onNodeWithContentDescription("Right Score")
            .assertIsDisplayed()
            .assert(hasText("0"))
            .assertHasClickAction()
    }

    @Test
    fun `displays player names`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText(LHS_PLAYER_NAME).assertIsDisplayed()

        rule.onNodeWithText(RHS_PLAYER_NAME).assertIsDisplayed()
    }

    @Test
    fun `when point is clicked then it is increased`() {
        rule.setContent { SutScreen() }

        rule
            .onNodeWithContentDescription("Right Score")
            .performClick()
            .assert(hasText("1"))
            .performClick()
            .performClick()
            .assert(hasText("3"))

        rule
            .onNodeWithContentDescription("Left Score")
            .performClick()
            .assert(hasText("1"))
            .performClick()
            .performClick()
            .assert(hasText("3"))
    }
}
