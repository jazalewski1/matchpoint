package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dev.jazalewski1.matchpoint.domain.tennis.PointOutcome
import dev.jazalewski1.matchpoint.feature.match.testdata.*
import dev.jazalewski1.matchpoint.feature.match.testfakes.FakeMatchController
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val testDispatcher: TestDispatcher = StandardTestDispatcher()) :
    TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}

private const val LHS_PLAYER_NAME = "Left player"
private const val RHS_PLAYER_NAME = "Right player"

private val sampleLhsPlayer = PlayerUiState(name = LHS_PLAYER_NAME, score = "0")
private val sampleRhsPlayer = PlayerUiState(name = RHS_PLAYER_NAME, score = "0")

private val sampleMatchUiState =
    MatchUiState(lhsPlayer = sampleLhsPlayer, rhsPlayer = sampleRhsPlayer)

private val minorIndicationFinishedDuration = MINOR_INDICATION_DURATION + 1.milliseconds
private val majorIndicationFinishedDuration = MAJOR_INDICATION_DURATION + 1.milliseconds

@RunWith(AndroidJUnit4::class)
class MatchViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle =
        SavedStateHandle(
            mapOf(
                "player1Name" to LHS_PLAYER_NAME,
                "player2Name" to RHS_PLAYER_NAME,
            )
        )

    @Test
    fun `initializes ui state`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(sampleMatchUiState)
        }
    }

    @Test
    fun `adds lhs score to match controller`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        viewModel.onLhsPressed()

        assertThat(matchController.addPointToLhsCount).isEqualTo(1)
    }

    @Test
    fun `adds rhs score to match controller`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        viewModel.onRhsPressed()

        assertThat(matchController.addPointToRhsCount).isEqualTo(1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updates ui state when adding lhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game15And0)

        viewModel.onLhsPressed()
        runCurrent()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsPlayer = sampleLhsPlayer.copy(score = "15", indication = Indication.Minor)
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }

        advanceTimeBy(minorIndicationFinishedDuration)

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsPlayer = sampleLhsPlayer.copy(score = "15", indication = null)
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updates ui state when adding rhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game0And15)

        viewModel.onRhsPressed()
        runCurrent()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    rhsPlayer = sampleRhsPlayer.copy(score = "15", indication = Indication.Minor)
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }

        advanceTimeBy(minorIndicationFinishedDuration)

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    rhsPlayer = sampleRhsPlayer.copy(score = "15", indication = null)
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updates ui state when adding lhs score and game won`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetCurrentGame(game40And15)
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(gameLoveAll)
        matchController.returnAddPointToLhs(PointOutcome.GameWon)

        viewModel.onLhsPressed()
        runCurrent()

        viewModel.uiState.test {
            val expectedLhs = sampleLhsPlayer.copy(score = "40", indication = Indication.Major)
            val expectedRhs = sampleRhsPlayer.copy(score = "15", indication = null)
            val expected = sampleMatchUiState.copy(lhsPlayer = expectedLhs, rhsPlayer = expectedRhs)
            assertThat(awaitItem()).isEqualTo(expected)
        }

        advanceTimeBy(majorIndicationFinishedDuration)

        viewModel.uiState.test {
            val expectedLhs = sampleLhsPlayer.copy(score = "0", indication = null)
            val expectedRhs = sampleRhsPlayer.copy(score = "0", indication = null)
            val expected = sampleMatchUiState.copy(lhsPlayer = expectedLhs, rhsPlayer = expectedRhs)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updates ui state when adding rhs score and game won`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetCurrentGame(game15And40)
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(gameLoveAll)
        matchController.returnAddPointToRhs(PointOutcome.GameWon)

        viewModel.onRhsPressed()
        runCurrent()

        viewModel.uiState.test {
            val expectedLhs = sampleLhsPlayer.copy(score = "15", indication = null)
            val expectedRhs = sampleRhsPlayer.copy(score = "40", indication = Indication.Major)
            val expected = sampleMatchUiState.copy(lhsPlayer = expectedLhs, rhsPlayer = expectedRhs)
            assertThat(awaitItem()).isEqualTo(expected)
        }

        advanceTimeBy(majorIndicationFinishedDuration)

        viewModel.uiState.test {
            val expectedLhs = sampleLhsPlayer.copy(score = "0", indication = null)
            val expectedRhs = sampleRhsPlayer.copy(score = "0", indication = null)
            val expected = sampleMatchUiState.copy(lhsPlayer = expectedLhs, rhsPlayer = expectedRhs)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `disables lhs scoring when indication is ongoing`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game15And40)
        matchController.returnAddPointToLhs(PointOutcome.PointScored)

        viewModel.onLhsPressed()
        runCurrent()

        assertThat(matchController.addPointToLhsCount).isEqualTo(1)
        viewModel.onLhsPressed()
        assertThat(matchController.addPointToLhsCount).isEqualTo(1)
        advanceTimeBy(minorIndicationFinishedDuration)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `disables rhs scoring when indication is ongoing`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game40And15)
        matchController.returnAddPointToRhs(PointOutcome.PointScored)

        viewModel.onRhsPressed()
        runCurrent()

        assertThat(matchController.addPointToRhsCount).isEqualTo(1)
        viewModel.onRhsPressed()
        assertThat(matchController.addPointToRhsCount).isEqualTo(1)
        advanceTimeBy(minorIndicationFinishedDuration)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updates both scores after lhs when controller does it`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game15And40)

        viewModel.onLhsPressed()
        runCurrent()

        viewModel.uiState.test {
            val actual = awaitItem()
            assertThat(actual.lhsPlayer.score).isEqualTo("15")
            assertThat(actual.rhsPlayer.score).isEqualTo("40")
        }
        advanceTimeBy(minorIndicationFinishedDuration)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updates both scores after rhs when controller does it`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game40And15)

        viewModel.onRhsPressed()
        runCurrent()

        viewModel.uiState.test {
            val actual = awaitItem()
            assertThat(actual.lhsPlayer.score).isEqualTo("40")
            assertThat(actual.rhsPlayer.score).isEqualTo("15")
        }
        advanceTimeBy(minorIndicationFinishedDuration)
    }
}
