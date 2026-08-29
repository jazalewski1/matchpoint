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

private val set1to2 = MatchDetails.Set(player1Games = 1, player2Games = 2, winner = Player.ONE)
private val set3to4 = MatchDetails.Set(player1Games = 3, player2Games = 4, winner = Player.TWO)
private val set5to6 = MatchDetails.Set(player1Games = 5, player2Games = 6, winner = Player.ONE)
private val setWithTieBreak =
    MatchDetails.Set(
        player1Games = 7,
        player2Games = 8,
        winner = Player.ONE,
        tieBreak = MatchDetails.Set.TieBreak(player1Points = 9, player2Points = 10),
    )

private val sampleMatchDetails =
    MatchDetails(
        player1Name = PLAYER1_NAME,
        player2Name = PLAYER2_NAME,
        sets = listOf(MatchDetails.Set(player1Games = 6, player2Games = 2, winner = Player.ONE)),
    )

@RunWith(AndroidJUnit4::class)
class MatchSummaryScreenTest {
    @get:Rule val rule = createComposeRule()

    @Composable
    private fun SutScreen(
        details: MatchDetails = sampleMatchDetails,
        onReturnClick: () -> Unit = {},
    ) =
        MatchSummaryScreen(
            details = details,
            onReturnClick = onReturnClick,
        )

    @Test
    fun `displays screen header`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText("Summary").assertIsDisplayed()
    }

    @Test
    fun `displays return button`() {
        rule.setContent { SutScreen() }

        rule.onNodeWithText("Return").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `displays table with full set headers when there are 3 sets`() {
        rule.setContent { SutScreen(details = sampleMatchDetails.copy(sets = List(3) { set3to4 })) }

        rule.onNodeWithTag("TableSetHeader1").assertIsDisplayed().assert(hasText("Set 1"))
        rule.onNodeWithTag("TableSetHeader2").assertIsDisplayed().assert(hasText("Set 2"))
        rule.onNodeWithTag("TableSetHeader3").assertIsDisplayed().assert(hasText("Set 3"))
    }

    @Test
    fun `displays table with full set headers when there are more than 3 sets`() {
        rule.setContent { SutScreen(details = sampleMatchDetails.copy(sets = List(5) { set3to4 })) }

        rule.onNodeWithTag("TableSetHeader1").assertIsDisplayed().assert(hasText("S1"))
        rule.onNodeWithTag("TableSetHeader2").assertIsDisplayed().assert(hasText("S2"))
        rule.onNodeWithTag("TableSetHeader3").assertIsDisplayed().assert(hasText("S3"))
        rule.onNodeWithTag("TableSetHeader4").assertIsDisplayed().assert(hasText("S4"))
        rule.onNodeWithTag("TableSetHeader5").assertIsDisplayed().assert(hasText("S5"))
    }

    @Test
    fun `displays table with player names`() {
        rule.setContent { SutScreen(details = sampleMatchDetails.copy(sets = List(5) { set3to4 })) }

        rule.onNodeWithTag("TablePlayer1Name").assert(hasText(PLAYER1_NAME))
        rule.onNodeWithTag("TablePlayer2Name").assert(hasText(PLAYER2_NAME))
    }

    @Test
    fun `displays table with set scores`() {
        rule.setContent {
            SutScreen(
                details =
                    sampleMatchDetails.copy(
                        sets =
                            listOf(
                                set1to2,
                                set3to4,
                                set5to6,
                                setWithTieBreak,
                            )
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
