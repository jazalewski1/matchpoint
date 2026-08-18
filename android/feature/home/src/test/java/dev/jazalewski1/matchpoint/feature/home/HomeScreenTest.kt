package dev.jazalewski1.matchpoint.feature.home

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun displaysLogo() {
        rule.setContent { HomeScreen() }

        rule
            .onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            .assertIsDisplayed()
            .assertContentDescriptionEquals("Matchpoint Logo")
    }

    @Test
    fun displaysButtons() {
        rule.setContent { HomeScreen() }

        rule.onNodeWithText("Start New Match").assertIsDisplayed().assertHasClickAction()
    }
}
