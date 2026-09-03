package dev.jazalewski1.matchpoint.feature.match.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.jazalewski1.matchpoint.core.common.Side
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        isTieBreak: Boolean = false,
        events: SharedFlow<MatchUiEvent> = MutableSharedFlow(),
        onExit: (Long) -> Unit = {},
        onMatchFinished: () -> Unit = {},
        navigationEvents: Flow<MatchNavigationEvent> = flowOf(),
    ) =
        MatchScreen(
            lhsPlayerName = lhsPlayerName,
            rhsPlayerName = rhsPlayerName,
            lhsScore = lhsScore,
            rhsScore = rhsScore,
            onLhsClick = onLhsClick,
            onRhsClick = onRhsClick,
            isTieBreak = isTieBreak,
            events = events,
            onMatchFinished = onMatchFinished,
            onExit = onExit,
            navigationEvents = navigationEvents,
        )

    @Test
    fun `displays points in regular game`() {
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
    fun `displays points in tiebreak`() {
        rule.setContent { SutScreen(isTieBreak = true) }

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

        rule.onNodeWithText("TIE-BREAK").assertIsDisplayed()
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

    private fun MutableSharedFlow<MatchUiEvent>.emitAndWait(event: MatchUiEvent) {
        tryEmit(event)
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun advance(milliseconds: Long) {
        rule.mainClock.advanceTimeBy(milliseconds)
        rule.waitForIdle()
    }

    @Test
    fun `when received lhs finished game then displays game indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val sentLhsScore = "30"
        val sentRhsScore = "15"
        val event =
            MatchUiEvent.GameFinished(
                winner = Side.LHS,
                lhsScore = sentLhsScore,
                rhsScore = sentRhsScore,
            )

        rule.onNodeWithText("GAME").assertIsNotDisplayed()
        rule.onNodeWithText("30 : 15").assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithText("GAME").assertIsDisplayed()
        rule.onNodeWithText("30 : 15").assertIsDisplayed()
        advance(milliseconds = DIALOG_INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithText("GAME").assertIsNotDisplayed()
        rule.onNodeWithText("30 : 15").assertIsNotDisplayed()
    }

    @Test
    fun `when received rhs finished game then displays game indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val sentLhsScore = "15"
        val sentRhsScore = "30"
        val event =
            MatchUiEvent.GameFinished(
                winner = Side.RHS,
                lhsScore = sentLhsScore,
                rhsScore = sentRhsScore,
            )

        rule.onNodeWithText("GAME").assertIsNotDisplayed()
        rule.onNodeWithText("30 : 15").assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithText("GAME").assertIsDisplayed()
        rule.onNodeWithText("15 : 30").assertIsDisplayed()
        advance(milliseconds = DIALOG_INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithText("GAME").assertIsNotDisplayed()
        rule.onNodeWithText("30 : 15").assertIsNotDisplayed()
    }

    @Test
    fun `when received lhs finished set then displays set indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val sentLhsScore = "3"
        val sentRhsScore = "1"
        val event =
            MatchUiEvent.SetFinished(
                winner = Side.LHS,
                lhsScore = sentLhsScore,
                rhsScore = sentRhsScore,
            )

        val header = "SET"
        val scoreText = "3 : 1"

        rule.onNodeWithText(header).assertIsNotDisplayed()
        rule.onNodeWithText(scoreText).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithText(header).assertIsDisplayed()
        rule.onNodeWithText(scoreText).assertIsDisplayed()
        advance(milliseconds = DIALOG_INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithText(header).assertIsNotDisplayed()
        rule.onNodeWithText(scoreText).assertIsNotDisplayed()
    }

    @Test
    fun `when received rhs finished set then displays set indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val sentLhsScore = "1"
        val sentRhsScore = "3"
        val event =
            MatchUiEvent.SetFinished(
                winner = Side.RHS,
                lhsScore = sentLhsScore,
                rhsScore = sentRhsScore,
            )

        val header = "SET"
        val scoreText = "1 : 3"

        rule.onNodeWithText(header).assertIsNotDisplayed()
        rule.onNodeWithText(scoreText).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithText(header).assertIsDisplayed()
        rule.onNodeWithText(scoreText).assertIsDisplayed()
        advance(milliseconds = DIALOG_INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithText(header).assertIsNotDisplayed()
        rule.onNodeWithText(scoreText).assertIsNotDisplayed()
    }

    @Test
    fun `when received lhs finished match then displays match indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val sentLhsScore = "3"
        val sentRhsScore = "1"
        val event =
            MatchUiEvent.MatchFinished(
                winner = Side.LHS,
                lhsScore = sentLhsScore,
                rhsScore = sentRhsScore,
            )

        val header = "MATCH"
        val scoreText = "3 : 1"

        rule.onNodeWithText(header).assertIsNotDisplayed()
        rule.onNodeWithText(scoreText).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithText(header).assertIsDisplayed()
        rule.onNodeWithText(scoreText).assertIsDisplayed()
        advance(milliseconds = DIALOG_INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithText(header).assertIsDisplayed()
        rule.onNodeWithText(scoreText).assertIsDisplayed()
    }

    @Test
    fun `when received rhs finished match then displays match indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val sentLhsScore = "1"
        val sentRhsScore = "3"
        val event =
            MatchUiEvent.MatchFinished(
                winner = Side.RHS,
                lhsScore = sentLhsScore,
                rhsScore = sentRhsScore,
            )

        val header = "MATCH"
        val scoreText = "1 : 3"

        rule.onNodeWithText(header).assertIsNotDisplayed()
        rule.onNodeWithText(scoreText).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithText(header).assertIsDisplayed()
        rule.onNodeWithText(scoreText).assertIsDisplayed()
        advance(milliseconds = DIALOG_INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithText(header).assertIsDisplayed()
        rule.onNodeWithText(scoreText).assertIsDisplayed()
    }

    @Test
    fun `when clicked on match indication then triggers match finished`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        var triggered = false
        rule.setContent { SutScreen(events = events, onMatchFinished = { triggered = true }) }

        val event =
            MatchUiEvent.MatchFinished(
                winner = Side.RHS,
                lhsScore = "1",
                rhsScore = "3",
            )

        events.emitAndWait(event)
        rule.onNodeWithText("MATCH").performClick()

        assertTrue(triggered)
    }

    @Test
    fun `when received navigation match finished then triggers on exit`() = runTest {
        var triggeredMatchId: Long? = null
        val navigationEventsChannel = Channel<MatchNavigationEvent>()
        rule.setContent {
            SutScreen(
                onExit = { id -> triggeredMatchId = id },
                navigationEvents = navigationEventsChannel.receiveAsFlow(),
            )
        }

        val matchId = 5L
        navigationEventsChannel.send(MatchNavigationEvent.MatchFinished(matchId = matchId))
        rule.awaitIdle()
        assertEquals(matchId, triggeredMatchId)
    }
}
