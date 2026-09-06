package dev.jazalewski1.matchpoint.feature.match.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.feature.match.main.detail.INDICATION_TOTAL_DURATION_MS
import dev.jazalewski1.matchpoint.feature.match.main.detail.POINT_INDICATION_TOTAL_DURATION_MS
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

private const val SWITCH_IND_CONTENT_DESC = "Side Switch Indication"
private const val GAME_IND_CONTENT_DESC = "Game Indication"
private const val SET_IND_CONTENT_DESC = "Set Indication"
private const val MATCH_IND_CONTENT_DESC = "Match Indication"
private const val LEFT_SCORE_CONTENT_DESC = "Left Score"
private const val RIGHT_SCORE_CONTENT_DESC = "Right Score"

private const val OVERSHOOT_MS = 1000L

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
            .onNodeWithContentDescription(LEFT_SCORE_CONTENT_DESC)
            .assertIsDisplayed()
            .assert(hasText(LHS_SCORE))
            .assertHasClickAction()

        rule
            .onNodeWithContentDescription(RIGHT_SCORE_CONTENT_DESC)
            .assertIsDisplayed()
            .assert(hasText(RHS_SCORE))
            .assertHasClickAction()
    }

    @Test
    fun `displays points in tiebreak`() {
        rule.setContent { SutScreen(isTieBreak = true) }

        rule
            .onNodeWithContentDescription(LEFT_SCORE_CONTENT_DESC)
            .assertIsDisplayed()
            .assert(hasText(LHS_SCORE))
            .assertHasClickAction()

        rule
            .onNodeWithContentDescription(RIGHT_SCORE_CONTENT_DESC)
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
    fun `registers click on lhs score`() {
        var triggered = false
        rule.setContent { SutScreen(onLhsClick = { triggered = true }) }

        rule.onNodeWithContentDescription(LEFT_SCORE_CONTENT_DESC).performClick()
        assertTrue(triggered)
    }

    @Test
    fun `registers click on rhs score`() {
        var triggered = false
        rule.setContent { SutScreen(onRhsClick = { triggered = true }) }

        rule.onNodeWithContentDescription(RIGHT_SCORE_CONTENT_DESC).performClick()
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
    fun `displays point indication without switch`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event = MatchUiEvent.PointScored(winner = Side.LHS, withSideSwitch = false)

        events.emitAndWait(event)
        advance(milliseconds = POINT_INDICATION_TOTAL_DURATION_MS.toLong() + OVERSHOOT_MS)
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `displays point indication with switch`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event = MatchUiEvent.PointScored(winner = Side.LHS, withSideSwitch = true)

        events.emitAndWait(event)
        advance(milliseconds = POINT_INDICATION_TOTAL_DURATION_MS.toLong() + OVERSHOOT_MS)
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsDisplayed()
    }

    @Test
    fun `displays lhs game indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.GameFinished(
                winner = Side.LHS,
                lhsScore = "30",
                rhsScore = "15",
                withSideSwitch = false,
            )

        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).assertIsDisplayed()
        rule.onNodeWithText("GAME").assertIsDisplayed()
        rule.onNodeWithText("30 : 15").assertIsDisplayed()
        advance(milliseconds = INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).assertIsNotDisplayed()
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `displays rhs game indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.GameFinished(
                winner = Side.RHS,
                lhsScore = "15",
                rhsScore = "30",
                withSideSwitch = false,
            )

        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).assertIsDisplayed()
        rule.onNodeWithText("GAME").assertIsDisplayed()
        rule.onNodeWithText("15 : 30").assertIsDisplayed()
        advance(milliseconds = INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).assertIsNotDisplayed()
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `displays lhs set indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.SetFinished(
                winner = Side.LHS,
                lhsScore = "3",
                rhsScore = "1",
                withSideSwitch = false,
            )

        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).assertIsDisplayed()
        rule.onNodeWithText("SET").assertIsDisplayed()
        rule.onNodeWithText("3 : 1").assertIsDisplayed()
        advance(milliseconds = INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).assertIsNotDisplayed()
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `displays rhs set indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.SetFinished(
                winner = Side.RHS,
                lhsScore = "1",
                rhsScore = "3",
                withSideSwitch = false,
            )

        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).assertIsDisplayed()
        rule.onNodeWithText("SET").assertIsDisplayed()
        rule.onNodeWithText("1 : 3").assertIsDisplayed()
        advance(milliseconds = INDICATION_TOTAL_DURATION_MS.toLong())
        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).assertIsNotDisplayed()
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `displays lhs match indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.MatchFinished(
                winner = Side.LHS,
                lhsScore = "3",
                rhsScore = "1",
            )

        rule.onNodeWithContentDescription(MATCH_IND_CONTENT_DESC).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithContentDescription(MATCH_IND_CONTENT_DESC).assertIsDisplayed()
        rule.onNodeWithText("MATCH").assertIsDisplayed()
        rule.onNodeWithText("3 : 1").assertIsDisplayed()
    }

    @Test
    fun `displays rhs match indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.MatchFinished(
                winner = Side.RHS,
                lhsScore = "1",
                rhsScore = "3",
            )

        rule.onNodeWithContentDescription(MATCH_IND_CONTENT_DESC).assertIsNotDisplayed()
        events.emitAndWait(event)
        rule.onNodeWithContentDescription(MATCH_IND_CONTENT_DESC).assertIsDisplayed()
        rule.onNodeWithText("MATCH").assertIsDisplayed()
        rule.onNodeWithText("1 : 3").assertIsDisplayed()
    }

    @Test
    fun `displays side switch indication`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.SetFinished(
                winner = Side.LHS,
                lhsScore = "1",
                rhsScore = "3",
                withSideSwitch = true,
            )
        events.emitAndWait(event)
        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).performClick()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsDisplayed()
    }

    @Test
    fun `hides game indication on click`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.GameFinished(
                winner = Side.LHS,
                lhsScore = "30",
                rhsScore = "15",
                withSideSwitch = false,
            )

        events.emitAndWait(event)
        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).performClick()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithContentDescription(GAME_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `hides switch indication on click`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event = MatchUiEvent.PointScored(winner = Side.RHS, withSideSwitch = true)

        events.emitAndWait(event)
        advance(milliseconds = POINT_INDICATION_TOTAL_DURATION_MS.toLong() + OVERSHOOT_MS)
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).performClick()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithContentDescription(SWITCH_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `hides set indication on click`() = runTest {
        rule.mainClock.autoAdvance = false

        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent { SutScreen(events = events) }

        val event =
            MatchUiEvent.SetFinished(
                winner = Side.LHS,
                lhsScore = "3",
                rhsScore = "1",
                withSideSwitch = false,
            )

        events.emitAndWait(event)
        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).performClick()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithContentDescription(SET_IND_CONTENT_DESC).assertIsNotDisplayed()
    }

    @Test
    fun `finishes on match indication click`() = runTest {
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
        rule.onNodeWithContentDescription(MATCH_IND_CONTENT_DESC).performClick()
        assertTrue(triggered)
    }

    @Test
    fun `exits on navigation match finished`() = runTest {
        var triggeredMatchId: Long? = null
        val navigationEventsChannel = Channel<MatchNavigationEvent>()
        rule.setContent {
            SutScreen(
                onExit = { id -> triggeredMatchId = id },
                navigationEvents = navigationEventsChannel.receiveAsFlow(),
            )
        }

        val matchId = 5L
        navigationEventsChannel.send(MatchNavigationEvent.Finish(matchId = matchId))
        rule.awaitIdle()
        assertEquals(matchId, triggeredMatchId)
    }
}
