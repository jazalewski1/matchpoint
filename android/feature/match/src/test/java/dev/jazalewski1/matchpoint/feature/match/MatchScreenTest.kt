package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MatchScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `displays initial points`() {
        rule.setContent { MatchScreen() }

        rule.onNodeWithContentDescription("Left Score")
            .assertIsDisplayed()
            .assert(hasText("0"))
            .assertHasClickAction()

        rule.onNodeWithContentDescription("Right Score")
            .assertIsDisplayed()
            .assert(hasText("0"))
            .assertHasClickAction()
    }

    @Test
    fun `displays player names`() {
        rule.setContent { MatchScreen() }

        rule.onNodeWithText("Player 1")
            .assertIsDisplayed()

        rule.onNodeWithText("Player 2")
            .assertIsDisplayed()
    }

    @Test
    fun `when point is clicked then it is increased`() {
        rule.setContent { MatchScreen() }

        rule.onNodeWithContentDescription("Right Score")
            .performClick()
            .assert(hasText("1"))
            .performClick()
            .performClick()
            .assert(hasText("3"))

        rule.onNodeWithContentDescription("Left Score")
            .performClick()
            .assert(hasText("1"))
            .performClick()
            .performClick()
            .assert(hasText("3"))
    }
}