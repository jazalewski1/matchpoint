package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val LHS_PLAYER_NAME = "Left player"
private const val RHS_PLAYER_NAME = "Right player"
private const val LHS_SCORE = "40"
private const val RHS_SCORE = "15"

private val minorLhsIndication =
    MatchUiEvent.Indication(type = IndicationType.Minor, side = Side.LHS)
private val minorRhsIndication =
    MatchUiEvent.Indication(type = IndicationType.Minor, side = Side.RHS)
private val majorLhsIndication =
    MatchUiEvent.Indication(type = IndicationType.Major, side = Side.LHS)
private val majorRhsIndication =
    MatchUiEvent.Indication(type = IndicationType.Major, side = Side.RHS)

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
        onIndicationComplete: (MatchUiEvent) -> Unit = {},
        events: SharedFlow<MatchUiEvent> = MutableSharedFlow(),
    ) =
        MatchScreen(
            lhsPlayerName = lhsPlayerName,
            rhsPlayerName = rhsPlayerName,
            lhsScore = lhsScore,
            rhsScore = rhsScore,
            onLhsClick = onLhsClick,
            onRhsClick = onRhsClick,
            onIndicationComplete = onIndicationComplete,
            events = events,
        )

    @Test
    fun `displays initial points`() {
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

    private fun testCompletion(event: MatchUiEvent) {
        rule.mainClock.autoAdvance = false

        var actualEvent: MatchUiEvent? = null
        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent {
            SutScreen(
                onIndicationComplete = { incoming -> actualEvent = incoming },
                events = events,
            )
        }
        events.emitAndWait(event)
        advance(milliseconds = 10_000)

        assertEquals(event, actualEvent)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when minor lhs indication event arrives then completes animation`() = runTest {
        testCompletion(event = minorLhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when minor rhs indication event arrives then completes animation`() = runTest {
        testCompletion(event = minorRhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when major lhs indication event arrives then completes animation`() = runTest {
        testCompletion(event = majorLhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when major rhs indication event arrives then completes animation`() = runTest {
        testCompletion(event = majorRhsIndication)
    }

    private fun testInterruption(
        firstEvent: MatchUiEvent,
        secondEvent: MatchUiEvent,
    ) {
        rule.mainClock.autoAdvance = false

        val completedEvents = mutableListOf<MatchUiEvent>()
        val events = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
        rule.setContent {
            SutScreen(
                onIndicationComplete = { incoming -> completedEvents.add(incoming) },
                events = events,
            )
        }
        events.emitAndWait(firstEvent)
        advance(milliseconds = 100)

        events.emitAndWait(secondEvent)
        advance(milliseconds = 10_000)

        assertThat(completedEvents).containsExactly(firstEvent, secondEvent)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when minor lhs indication event interrupts lhs indication then both complete`() = runTest {
        testInterruption(firstEvent = minorLhsIndication, secondEvent = minorLhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when minor rhs indication event interrupts rhs indication then both complete`() = runTest {
        testInterruption(firstEvent = minorRhsIndication, secondEvent = minorRhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when major lhs indication event interrupts lhs indication then both complete`() = runTest {
        testInterruption(firstEvent = minorLhsIndication, secondEvent = minorLhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when major rhs indication event interrupts lhs indication then both complete`() = runTest {
        testInterruption(firstEvent = minorRhsIndication, secondEvent = minorRhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when minor lhs indication event interrupts rhs indication then both complete`() = runTest {
        testInterruption(firstEvent = minorLhsIndication, secondEvent = minorRhsIndication)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when minor rhs indication event interrupts lhs indication then both complete`() = runTest {
        testInterruption(firstEvent = minorRhsIndication, secondEvent = minorLhsIndication)
    }
}
