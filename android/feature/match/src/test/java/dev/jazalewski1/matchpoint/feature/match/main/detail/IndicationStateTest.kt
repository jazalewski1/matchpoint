package dev.jazalewski1.matchpoint.feature.match.main.detail

import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.feature.match.main.MatchUiEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

private const val score1 = "1"
private const val score2 = "2"

private val pointUiEvent =
    MatchUiEvent.PointScored(
        winner = Side.LHS,
        withSideSwitch = false,
    )
private val gameUiEvent =
    MatchUiEvent.GameFinished(
        winner = Side.LHS,
        lhsScore = score1,
        rhsScore = score2,
        withSideSwitch = false,
    )
private val setUiEvent =
    MatchUiEvent.SetFinished(
        winner = Side.LHS,
        lhsScore = "1",
        rhsScore = "3",
        withSideSwitch = false,
    )
private val matchUiEvent =
    MatchUiEvent.MatchFinished(
        winner = Side.LHS,
        lhsScore = "1",
        rhsScore = "3",
    )

private val pointConfig = IndicationConfig.Point(side = Side.LHS)
private val gameConfig =
    IndicationConfig.Major.Game(side = Side.LHS, lhsScore = score1, rhsScore = score2)
private val setConfig = IndicationConfig.Major.Set(side = Side.LHS, lhsScore = "1", rhsScore = "3")
private val matchConfig =
    IndicationConfig.Major.Match(side = Side.LHS, lhsScore = "1", rhsScore = "3")
private val sideSwitchConfig = IndicationConfig.Major.SideSwitch

@OptIn(ExperimentalCoroutinesApi::class)
class IndicationStateTest {
    @Test
    fun `runs point indication without side switch`() = runTest {
        val state = IndicationState(this)

        state.process(pointUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(pointConfig)

        advanceTimeBy(POINT_INDICATION_TOTAL_DURATION_MS.milliseconds)
        runCurrent()
        assertThat(state.indicationConfig).isNull()
    }

    @Test
    fun `runs point indication with side switch`() = runTest {
        val state = IndicationState(this)

        state.process(pointUiEvent.copy(withSideSwitch = true))
        runCurrent()
        advanceTimeBy(POINT_INDICATION_TOTAL_DURATION_MS.milliseconds)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(IndicationConfig.Major.SideSwitch)
    }

    @Test
    fun `interrupts point indication`() = runTest {
        val state = IndicationState(this)

        state.process(pointUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(pointConfig)

        advanceTimeBy(POINT_INDICATION_TOTAL_DURATION_MS.milliseconds / 2)
        runCurrent()
        state.process(gameUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(gameConfig)
    }

    @Test
    fun `dismisses point indication`() = runTest {
        val state = IndicationState(this)

        state.process(pointUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(pointConfig)

        advanceTimeBy(POINT_INDICATION_TOTAL_DURATION_MS.milliseconds / 2)
        runCurrent()
        state.dismissIndication()
        runCurrent()
        assertThat(state.indicationConfig).isNull()
    }

    @Test
    fun `runs game indication without side switch`() = runTest {
        val state = IndicationState(this)

        state.process(gameUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(gameConfig)
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds)
        runCurrent()
        assertThat(state.indicationConfig).isNull()
    }

    @Test
    fun `runs game indication with side switch`() = runTest {
        val state = IndicationState(this)

        state.process(gameUiEvent.copy(withSideSwitch = true))
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(gameConfig)
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(sideSwitchConfig)
    }

    @Test
    fun `interrupts game indication`() = runTest {
        val state = IndicationState(this)

        state.process(gameUiEvent)
        runCurrent()
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds / 2)
        runCurrent()
        state.process(setUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(setConfig)
    }

    @Test
    fun `dismisses game indication`() = runTest {
        val state = IndicationState(this)

        state.process(gameUiEvent)
        runCurrent()
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds / 2)
        runCurrent()
        state.dismissIndication()
        runCurrent()
        assertThat(state.indicationConfig).isNull()
    }

    @Test
    fun `runs set indication without side switch`() = runTest {
        val state = IndicationState(this)

        state.process(setUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(setConfig)
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds)
        runCurrent()
        assertThat(state.indicationConfig).isNull()
    }

    @Test
    fun `runs set indication with side switch`() = runTest {
        val state = IndicationState(this)

        state.process(setUiEvent.copy(withSideSwitch = true))
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(setConfig)
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(sideSwitchConfig)
    }

    @Test
    fun `interrupts set indication`() = runTest {
        val state = IndicationState(this)

        state.process(setUiEvent)
        runCurrent()
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds / 2)
        runCurrent()
        state.process(matchUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(matchConfig)
    }

    @Test
    fun `dismisses set indication`() = runTest {
        val state = IndicationState(this)

        state.process(setUiEvent)
        runCurrent()
        advanceTimeBy(INDICATION_TOTAL_DURATION_MS.milliseconds / 2)
        runCurrent()
        state.dismissIndication()
        runCurrent()
        assertThat(state.indicationConfig).isNull()
    }

    @Test
    fun `runs match indication`() = runTest {
        val state = IndicationState(this)

        state.process(matchUiEvent)
        runCurrent()
        assertThat(state.indicationConfig).isEqualTo(matchConfig)
    }

    @Test
    fun `dismisses match indication`() = runTest {
        val state = IndicationState(this)

        state.process(matchUiEvent)
        runCurrent()
        state.dismissIndication()
        runCurrent()
        assertThat(state.indicationConfig).isNull()
    }
}
