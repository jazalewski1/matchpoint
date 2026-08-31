package dev.jazalewski1.matchpoint.feature.match.main

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.data.MatchDetails
import dev.jazalewski1.matchpoint.core.data.MemoryMatchRepository
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchHistory
import dev.jazalewski1.matchpoint.domain.tennis.SetState
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

private val sampleLhsPlayer = UiState.Player(name = LHS_PLAYER_NAME, score = "0")
private val sampleRhsPlayer = UiState.Player(name = RHS_PLAYER_NAME, score = "0")

private val sampleGameUiState =
    UiState.Game(lhsPlayer = sampleLhsPlayer, rhsPlayer = sampleRhsPlayer, isTieBreak = false)
private val sampleMatchUiState = UiState(game = sampleGameUiState)

@RunWith(AndroidJUnit4::class)
class MatchViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val matchRepository = MemoryMatchRepository()

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
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(sampleMatchUiState)
        }
    }

    @Test
    fun `adds lhs score to match controller`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        viewModel.onLhsPressed()

        assertThat(matchController.addPointToLhsCount).isEqualTo(1)
    }

    @Test
    fun `adds rhs score to match controller`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        viewModel.onRhsPressed()

        assertThat(matchController.addPointToRhsCount).isEqualTo(1)
    }

    @Test
    fun `updates ui state when adding lhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.afterAddPointToLhs {
            matchController.returnGetState(initialMatch.copy(game = game15And40))
        }

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = sampleLhsPlayer.copy(score = "15"),
                            rhsPlayer = sampleRhsPlayer.copy(score = "40"),
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding rhs score`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.afterAddPointToRhs {
            matchController.returnGetState(initialMatch.copy(game = game40And15))
        }

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = sampleLhsPlayer.copy(score = "40"),
                            rhsPlayer = sampleRhsPlayer.copy(score = "15"),
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about point scored when lhs scores`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToLhs(MatchEvent.PointScored)

        viewModel.uiEvents.test {
            viewModel.onLhsPressed()

            val expected = MatchUiEvent.PointScored(winner = Side.LHS)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about point scored when rhs scores`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToRhs(MatchEvent.PointScored)

        viewModel.uiEvents.test {
            viewModel.onRhsPressed()

            val expected = MatchUiEvent.PointScored(winner = Side.RHS)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates score when lhs wins game`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetState(initialMatch.copy(game = game40And15))
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToLhs(MatchEvent.GameWon)
        matchController.afterAddPointToLhs {
            matchController.returnGetState(initialMatch.copy(game = gameLoveAll, set = set1To0))
        }

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected = sampleMatchUiState
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates score when rhs wins game`() = runTest {
        val matchController = FakeMatchController()
        matchController.returnGetState(initialMatch.copy(game = game15And40))
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToRhs(MatchEvent.GameWon)
        matchController.afterAddPointToRhs {
            matchController.returnGetState(initialMatch.copy(game = gameLoveAll, set = set0To1))
        }

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected = sampleMatchUiState
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about game finished when lhs wins game`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToLhs(MatchEvent.GameWon)
        matchController.afterAddPointToLhs {
            matchController.returnGetState(
                initialMatch.copy(game = gameLoveAll, set = SetState(lhsGames = 5, rhsGames = 3))
            )
        }

        viewModel.uiEvents.test {
            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.LHS,
                    lhsScore = "5",
                    rhsScore = "3",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about game finished when rhs wins game`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToRhs(MatchEvent.GameWon)
        matchController.afterAddPointToRhs {
            matchController.returnGetState(
                initialMatch.copy(game = gameLoveAll, set = SetState(lhsGames = 3, rhsGames = 5))
            )
        }

        viewModel.uiEvents.test {
            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.RHS,
                    lhsScore = "3",
                    rhsScore = "5",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about set finished when lhs wins set`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToLhs(MatchEvent.SetWon)
        matchController.afterAddPointToLhs {
            matchController.returnGetState(
                initialMatch.copy(game = gameLoveAll, set = set0To0, match = match1To0)
            )
        }

        viewModel.uiEvents.test {
            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.SetFinished(
                    winner = Side.LHS,
                    lhsScore = "1",
                    rhsScore = "0",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about set finished when rhs wins set`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToRhs(MatchEvent.SetWon)
        matchController.afterAddPointToRhs {
            matchController.returnGetState(
                initialMatch.copy(game = gameLoveAll, set = set0To0, match = match0To1)
            )
        }

        viewModel.uiEvents.test {
            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.SetFinished(
                    winner = Side.RHS,
                    lhsScore = "0",
                    rhsScore = "1",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding lhs score in tiebreak`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.afterAddPointToLhs {
            matchController.returnGetState(initialMatch.copy(game = tiebreak2To5))
        }

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = sampleLhsPlayer.copy(score = "2"),
                            rhsPlayer = sampleRhsPlayer.copy(score = "5"),
                            isTieBreak = true,
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding rhs score in tiebreak`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.afterAddPointToRhs {
            matchController.returnGetState(initialMatch.copy(game = tiebreak5To2))
        }

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = sampleLhsPlayer.copy(score = "5"),
                            rhsPlayer = sampleRhsPlayer.copy(score = "2"),
                            isTieBreak = true,
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about set finished when lhs wins match`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToLhs(MatchEvent.MatchWon)
        matchController.afterAddPointToLhs {
            matchController.returnGetState(
                initialMatch.copy(game = game40And15, set = set6To1, match = match3To1)
            )
        }

        viewModel.uiEvents.test {
            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.MatchFinished(
                    winner = Side.LHS,
                    lhsScore = "3",
                    rhsScore = "1",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about set finished when rhs wins match`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnAddPointToRhs(MatchEvent.MatchWon)
        matchController.afterAddPointToRhs {
            matchController.returnGetState(
                initialMatch.copy(game = game15And40, set = set1To6, match = match1To3)
            )
        }

        viewModel.uiEvents.test {
            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.MatchFinished(
                    winner = Side.RHS,
                    lhsScore = "1",
                    rhsScore = "3",
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `stores match details in repository on finished`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        matchController.returnGetHistory(
            MatchHistory(
                sets =
                    listOf(
                        MatchHistory.Set(
                            player1Games = 6,
                            player2Games = 4,
                            winner = Player.ONE,
                        ),
                        MatchHistory.Set(
                            player1Games = 7,
                            player2Games = 6,
                            winner = Player.ONE,
                            tieBreak =
                                MatchHistory.Set.TieBreak(
                                    player1Points = 7,
                                    player2Points = 0,
                                ),
                        ),
                        MatchHistory.Set(
                            player1Games = 6,
                            player2Games = 2,
                            winner = Player.ONE,
                        ),
                    )
            )
        )

        viewModel.onFinished()

        val actual = matchRepository.getMatch(0)
        val expected =
            MatchDetails(
                player1Name = LHS_PLAYER_NAME,
                player2Name = RHS_PLAYER_NAME,
                sets =
                    listOf(
                        MatchDetails.Set(
                            player1Games = 6,
                            player2Games = 4,
                            winner = Player.ONE,
                        ),
                        MatchDetails.Set(
                            player1Games = 7,
                            player2Games = 6,
                            winner = Player.ONE,
                            tieBreak =
                                MatchDetails.Set.TieBreak(
                                    player1Points = 7,
                                    player2Points = 0,
                                ),
                        ),
                        MatchDetails.Set(
                            player1Games = 6,
                            player2Games = 2,
                            winner = Player.ONE,
                        ),
                    ),
            )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `sends finished match event on finished`() = runTest {
        val matchController = FakeMatchController()
        val viewModel = MatchViewModel(matchController, matchRepository, savedStateHandle)

        viewModel.navigationEvents.test {
            viewModel.onFinished()
            assertThat(awaitItem()).isEqualTo(MatchNavigationEvent.MatchFinished(matchId = 0))
        }
    }
}
