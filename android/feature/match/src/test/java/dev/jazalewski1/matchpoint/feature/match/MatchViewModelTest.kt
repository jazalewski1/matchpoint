package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchState
import dev.jazalewski1.matchpoint.domain.tennis.Points
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

private val sampleMatchUiState =
    MatchUiState(
        lhsPlayerName = LHS_PLAYER_NAME,
        rhsPlayerName = RHS_PLAYER_NAME,
        lhsScore = "0",
        rhsScore = "0",
    )

class FakeMatchController : MatchController {
    var addedLhsScore = false
        private set

    var addedRhsScore = false
        private set

    private var matchState = sampleMatchState

    fun setState(new: MatchState) {
        matchState = new
    }

    override fun getState() = matchState

    override fun addLhsScore() {
        addedLhsScore = true
    }

    override fun addRhsScore() {
        addedRhsScore = true
    }
}

private val sampleMatchState = MatchState(game = GameState.Ongoing(Points.LOVE, Points.LOVE))

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

        viewModel.addLhsScore()

        assertThat(matchController.addedLhsScore).isTrue()
    }

    @Test
    fun `adds rhs score to match controller`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        viewModel.addRhsScore()

        assertThat(matchController.addedRhsScore).isTrue()
    }

    @Test
    fun `updates ui state when adding lhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        val newMatchState =
            sampleMatchState.copy(game = GameState.Ongoing(Points.FIFTEEN, Points.LOVE))
        matchController.setState(newMatchState)

        viewModel.addLhsScore()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsScore = "15",
                    rhsScore = "0",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding rhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, savedStateHandle)

        val newMatchState =
            sampleMatchState.copy(game = GameState.Ongoing(Points.LOVE, Points.FIFTEEN))
        matchController.setState(newMatchState)

        viewModel.addRhsScore()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    lhsScore = "0",
                    rhsScore = "15",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }
}
