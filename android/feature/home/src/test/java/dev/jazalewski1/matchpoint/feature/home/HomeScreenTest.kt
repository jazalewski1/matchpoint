package dev.jazalewski1.matchpoint.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule val rule = createComposeRule()

    @Composable
    private fun SutScreen(onStartClick: () -> Unit = {}) = HomeScreen(onStartClick = onStartClick)

    @Test
    fun `displays logo`() {
        rule.setContent { SutScreen() }

        rule
            .onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            .assertIsDisplayed()
            .assertContentDescriptionEquals("Matchpoint Logo")
    }

    @Test
    fun `displays buttons`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText("Start New Match").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `when start button is clicked then callback is triggered`() {
        var triggered = false
        rule.setContent { SutScreen(onStartClick = { triggered = true }) }

        rule.onNodeWithText("Start New Match").performClick()

        assertTrue(triggered)
    }
}
