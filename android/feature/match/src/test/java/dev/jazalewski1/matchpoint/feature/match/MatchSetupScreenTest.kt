package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private fun hasEditableText(expected: String) =
    SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(expected))

fun isError() = SemanticsMatcher.keyIsDefined(SemanticsProperties.Error)

@RunWith(AndroidJUnit4::class)
class MatchSetupScreenTest {
    @get:Rule val rule = createComposeRule()

    @Composable
    private fun SutScreen(onStart: (String, String) -> Unit = { p1, p2 -> }) =
        MatchSetupScreen(onStart = onStart)

    @Test
    fun `display header`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText("New Match").assertIsDisplayed()
    }

    @Test
    fun `displays input fields for player names`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText("Player 1").assertIsDisplayed()
        rule.onNodeWithContentDescription("Player 1 Name").assertIsDisplayed().assert(hasText(""))

        rule.onNodeWithText("Player 2").assertIsDisplayed()
        rule.onNodeWithContentDescription("Player 2 Name").assertIsDisplayed().assert(hasText(""))

        rule.onAllNodesWithText("0 / 24").assertCountEquals(2)
    }

    @Test
    fun `receives player names`() {
        rule.setContent { SutScreen() }

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
        rule.setContent { SutScreen() }

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
        rule.setContent { SutScreen() }

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
    fun `when player name field is blank then error is displayed`() {
        rule.setContent { SutScreen() }

        fun test(contentDescription: String) {
            rule.onNodeWithContentDescription(contentDescription).apply {
                performTextReplacement("Name")
                performTextClearance()

                assert(isError())
            }

            rule.onNodeWithText("Name cannot be empty.").assertIsDisplayed()
        }

        test("Player 1 Name")
        test("Player 2 Name")
    }

    @Test
    fun `when player name field is too long then error is displayed`() {
        rule.setContent { SutScreen() }

        fun test(contentDescription: String) {
            rule.onNodeWithContentDescription(contentDescription).apply {
                performTextReplacement("a".repeat(200))

                assert(isError())
            }

            rule.onNodeWithText("Name cannot be longer than 24 characters.").assertIsDisplayed()
        }

        test("Player 1 Name")
        test("Player 2 Name")
    }

    @Test
    fun `displays disabled start button`() {
        rule.setContent { SutScreen() }

        rule
            .onNodeWithContentDescription("Starts new match")
            .assertIsDisplayed()
            .assert(hasText("Start"))
            .assertIsNotEnabled()
    }

    @Test
    fun `when names are present then start button is enabled`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithContentDescription("Player 1 Name").performTextReplacement("Roger")
        rule.onNodeWithContentDescription("Player 2 Name").performTextReplacement("Novak")

        rule.onNodeWithContentDescription("Starts new match").assertIsEnabled()
    }

    @Test
    fun `when there are input errors then start button is disabled`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithContentDescription("Player 1 Name").performTextReplacement("a".repeat(200))

        rule
            .onNodeWithContentDescription("Starts new match")
            .assertIsDisplayed()
            .assert(hasText("Start"))
            .assertIsNotEnabled()
    }

    @Test
    fun `when start button is clicked then callback with input names is triggered`() {
        var names: Pair<String, String>? = null
        rule.setContent { SutScreen(onStart = { n1, n2 -> names = Pair(n1, n2) }) }

        val name1 = "Roger"
        rule.onNodeWithContentDescription("Player 1 Name").performTextReplacement(name1)
        val name2 = "Rafael"
        rule.onNodeWithContentDescription("Player 2 Name").performTextReplacement(name2)
        rule.onNodeWithContentDescription("Starts new match").performClick()

        assertEquals(Pair(name1, name2), names)
    }
}
