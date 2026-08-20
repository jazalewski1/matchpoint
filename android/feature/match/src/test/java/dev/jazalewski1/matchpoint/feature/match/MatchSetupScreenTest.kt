package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private fun hasEditableText(expected: String) =
    SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(expected))

@RunWith(AndroidJUnit4::class)
class MatchSetupScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `display header`() {
        rule.setContent { MatchSetupScreen() }

        rule.onNodeWithText("New Match").assertIsDisplayed()
    }

    @Test
    fun `displays input fields for player names`() {
        rule.setContent { MatchSetupScreen() }

        rule.onNodeWithContentDescription("Player 1 Name")
            .assertIsDisplayed()
            .assert(hasText(""))

        rule.onNodeWithContentDescription("Player 2 Name")
            .assertIsDisplayed()
            .assert(hasText(""))
    }

    @Test
    fun `receives player names`() {
        rule.setContent { MatchSetupScreen() }

        rule.onNodeWithContentDescription("Player 1 Name").apply {
            val newName = "Roger"
            performTextReplacement(newName)
            assert(hasEditableText(newName))
        }

        rule.onNodeWithContentDescription("Player 2 Name").apply {
            val newName = "Novak"
            performTextReplacement(newName)
            assert(hasEditableText(newName))
        }
    }

    @Test
    fun `when first player name field clicked done then moves to second field`() {
        rule.setContent { MatchSetupScreen() }

        rule.onNodeWithContentDescription("Player 1 Name").apply {
            performClick()
            performTextReplacement("Name")
            assertIsFocused()
            performImeAction()
        }

        rule.onNodeWithContentDescription("Player 1 Name").assertIsNotFocused()
        rule.onNodeWithContentDescription("Player 2 Name").assertIsFocused()
    }

    @Test
    fun `when second player name field clicked done then keep focus`() {
        rule.setContent { MatchSetupScreen() }

        rule.onNodeWithContentDescription("Player 2 Name").apply {
            performClick()
            performTextReplacement("Name")
            assertIsFocused()
            performImeAction()
        }
        rule.onNodeWithContentDescription("Player 1 Name").assertIsNotFocused()
        // Unexpectedly seems like this is Android behavior -
        // input stays in focus after pressing Done action
        rule.onNodeWithContentDescription("Player 2 Name").assertIsFocused()
    }

    @Test
    fun `displays start button`() {
        rule.setContent { MatchSetupScreen() }

        rule.onNodeWithContentDescription("Starts new match")
            .assertIsDisplayed()
            .assert(hasText("Start"))
    }
}