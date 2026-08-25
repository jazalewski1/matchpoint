package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dev.jazalewski1.matchpoint.domain.tennis.PointOutcome
import dev.jazalewski1.matchpoint.feature.match.testdata.*
import dev.jazalewski1.matchpoint.feature.match.testfakes.FakeMatchController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
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

    @Test
    fun `updates ui state when adding lhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game15And40)

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsPlayer = sampleLhsPlayer.copy(score = "15"),
                    rhsPlayer = sampleRhsPlayer.copy(score = "40"),
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding rhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnGetCurrentGame(game40And15)

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsPlayer = sampleLhsPlayer.copy(score = "40"),
                    rhsPlayer = sampleRhsPlayer.copy(score = "15"),
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about point scored when lhs scores`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnAddPointToLhs(PointOutcome.PointScored)

        viewModel.uiEvents.test {
            viewModel.onLhsPressed()

            val expected = MatchUiEvent.PointScored(winner = Side.LHS)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about point scored when rhs scores`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetCurrentGame(game15And40)
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnAddPointToRhs(PointOutcome.PointScored)

        viewModel.uiEvents.test {
            viewModel.onRhsPressed()

            val expected = MatchUiEvent.PointScored(winner = Side.RHS)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates score when lhs wins game`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetCurrentGame(game40And15)
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnAddPointToLhs(PointOutcome.GameWon)
        matchController.returnGetCurrentGame(gameLoveAll)

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsPlayer = sampleLhsPlayer.copy(score = "0"),
                    rhsPlayer = sampleRhsPlayer.copy(score = "0"),
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates score when rhs wins game`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetCurrentGame(game15And40)
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnAddPointToRhs(PointOutcome.GameWon)
        matchController.returnGetCurrentGame(gameLoveAll)

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsPlayer = sampleLhsPlayer.copy(score = "0"),
                    rhsPlayer = sampleRhsPlayer.copy(score = "0"),
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about game finished when lhs wins game`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetCurrentGame(game40And15)
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnAddPointToLhs(PointOutcome.GameWon)

        viewModel.uiEvents.test {
            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.LHS,
                    lhsScore = "40",
                    rhsScore = "15",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about game finished when rhs wins game`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetCurrentGame(game15And40)
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        matchController.returnAddPointToRhs(PointOutcome.GameWon)

        viewModel.uiEvents.test {
            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.RHS,
                    lhsScore = "15",
                    rhsScore = "40",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }
}
