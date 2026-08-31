package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val PLAYER1_NAME = "Player 1"
private const val PLAYER2_NAME = "Player 2"

private fun setWon(games: Int, tbPoints: Int? = null) =
    SetUiState(games = games, isWinner = true, tieBreakPoints = tbPoints)

private fun setLost(games: Int, tbPoints: Int? = null) =
    SetUiState(games = games, isWinner = false, tieBreakPoints = tbPoints)

private data object Samples {
    val loadedUiState =
        MatchSummaryUiState.Loaded(
            player1 =
                PlayerSummaryUiState(
                    name = PLAYER1_NAME,
                    sets = listOf(setWon(6), setLost(1), setWon(6)),
                ),
            player2 =
                PlayerSummaryUiState(
                    name = PLAYER2_NAME,
                    sets = listOf(setLost(2), setWon(6), setLost(3)),
                ),
            numOfSets = 3,
        )
}

@RunWith(AndroidJUnit4::class)
class MatchSummaryScreenTest {
    @get:Rule val rule = createComposeRule()

    @Composable
    private fun SutScreen(
        uiState: MatchSummaryUiState = Samples.loadedUiState,
        onReturnClick: () -> Unit = {},
    ) =
        MatchSummaryScreen(
            uiState = uiState,
            onReturnClick = onReturnClick,
        )

    @Test
    fun `on loaded screen displays basic elements`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText("Summary").assertIsDisplayed()
        rule.onNodeWithText("Return").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `on error screen displays basic elements`() {
        val message = "Error message"
        rule.setContent { SutScreen(
            uiState = MatchSummaryUiState.Error(message = message)
        ) }

        rule.onNodeWithText("Summary").assertIsDisplayed()
        rule.onNodeWithText(message).assertIsDisplayed()
        rule.onNodeWithText("Return").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `on loaded screen displays table with full set headers when there are 3 sets`() {
        rule.setContent { SutScreen(uiState = Samples.loadedUiState) }

        rule.onNodeWithTag("TableSetHeader1").assertIsDisplayed().assert(hasText("Set 1"))
        rule.onNodeWithTag("TableSetHeader2").assertIsDisplayed().assert(hasText("Set 2"))
        rule.onNodeWithTag("TableSetHeader3").assertIsDisplayed().assert(hasText("Set 3"))
    }

    @Test
    fun `on loaded screen displays table with full set headers when there are more than 3 sets`() {
        rule.setContent {
            SutScreen(
                uiState =
                    MatchSummaryUiState.Loaded(
                        player1 =
                            PlayerSummaryUiState(name = PLAYER1_NAME, sets = List(4) { setWon(6) }),
                        player2 =
                            PlayerSummaryUiState(
                                name = PLAYER2_NAME,
                                sets = List(4) { setLost(6) },
                            ),
                        numOfSets = 4,
                    )
            )
        }

        rule.onNodeWithTag("TableSetHeader1").assertIsDisplayed().assert(hasText("S1"))
        rule.onNodeWithTag("TableSetHeader2").assertIsDisplayed().assert(hasText("S2"))
        rule.onNodeWithTag("TableSetHeader3").assertIsDisplayed().assert(hasText("S3"))
        rule.onNodeWithTag("TableSetHeader4").assertIsDisplayed().assert(hasText("S4"))
    }

    @Test
    fun `on loaded screen displays table with player names`() {
        rule.setContent { SutScreen(uiState = Samples.loadedUiState) }

        rule.onNodeWithTag("TablePlayer1Name").assert(hasText(PLAYER1_NAME))
        rule.onNodeWithTag("TablePlayer2Name").assert(hasText(PLAYER2_NAME))
    }

    @Test
    fun `on loaded screen displays table with set scores`() {
        rule.setContent {
            SutScreen(
                uiState =
                    MatchSummaryUiState.Loaded(
                        player1 =
                            PlayerSummaryUiState(
                                name = PLAYER1_NAME,
                                sets = listOf(setWon(1), setLost(3), setWon(5), setLost(7, 9)),
                            ),
                        player2 =
                            PlayerSummaryUiState(
                                name = PLAYER2_NAME,
                                sets = listOf(setWon(2), setLost(4), setWon(6), setLost(8, 10)),
                            ),
                        numOfSets = 4,
                    )
            )
        }

        rule.onNodeWithTag("TablePlayer1Set1").onChild().assertIsDisplayed().assert(hasText("1"))
        rule.onNodeWithTag("TablePlayer2Set1").onChild().assertIsDisplayed().assert(hasText("2"))
        rule.onNodeWithTag("TablePlayer1Set2").onChild().assertIsDisplayed().assert(hasText("3"))
        rule.onNodeWithTag("TablePlayer2Set2").onChild().assertIsDisplayed().assert(hasText("4"))
        rule.onNodeWithTag("TablePlayer1Set3").onChild().assertIsDisplayed().assert(hasText("5"))
        rule.onNodeWithTag("TablePlayer2Set3").onChild().assertIsDisplayed().assert(hasText("6"))
        rule.onNodeWithTag("TablePlayer1Set4").onChildren().apply {
            this[0].assertIsDisplayed().assert(hasText("7"))
            this[1].assertIsDisplayed().assert(hasText("9"))
        }
        rule.onNodeWithTag("TablePlayer2Set4").onChildren().apply {
            this[0].assertIsDisplayed().assert(hasText("8"))
            this[1].assertIsDisplayed().assert(hasText("10"))
        }
    }
}
