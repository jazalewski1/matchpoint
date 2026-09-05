package dev.jazalewski1.matchpoint.feature.match.main

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.core.data.MatchDetails
import dev.jazalewski1.matchpoint.core.data.MemoryMatchRepository
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchHistory
import dev.jazalewski1.matchpoint.domain.tennis.SideConfig
import dev.jazalewski1.matchpoint.feature.match.testdata.*
import dev.jazalewski1.matchpoint.feature.match.testfakes.FakeMatchController
import dev.jazalewski1.matchpoint.feature.match.testfakes.FakeMatchControllerFactory
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

private const val PLAYER1_NAME = "Player One"
private const val PLAYER2_NAME = "Player Two"

private const val NUM_OF_SETS_TO_WIN = 3

private val sampleLhsPlayer = UiState.Player(name = PLAYER1_NAME, score = "0")
private val sampleRhsPlayer = UiState.Player(name = PLAYER2_NAME, score = "0")

private val sampleGameUiState =
    UiState.Game(lhsPlayer = sampleLhsPlayer, rhsPlayer = sampleRhsPlayer, isTieBreak = false)
private val sampleMatchUiState = UiState(game = sampleGameUiState)

@RunWith(AndroidJUnit4::class)
class MatchViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val matchController = FakeMatchController()

    private val matchControllerFactory = FakeMatchControllerFactory(matchController)

    private val matchRepository = MemoryMatchRepository()

    private val savedStateHandle =
        SavedStateHandle(
            mapOf(
                "player1Name" to PLAYER1_NAME,
                "player2Name" to PLAYER2_NAME,
                "numOfSetsToWin" to NUM_OF_SETS_TO_WIN,
            )
        )

    @Test
    fun `initializes ui state`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(sampleMatchUiState)
        }
    }

    @Test
    fun `creates match controller with parameters`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        assertThat(matchControllerFactory.storedNumOfSetsToWin).isEqualTo(NUM_OF_SETS_TO_WIN)
    }

    @Test
    fun `adds lhs score to match controller`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.onLhsPressed()

        assertThat(matchController.addPointToLhsCount).isEqualTo(1)
    }

    @Test
    fun `adds rhs score to match controller`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.onRhsPressed()

        assertThat(matchController.addPointToRhsCount).isEqualTo(1)
    }

    @Test
    fun `updates ui state when adding lhs score with player1 on lhs`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.afterAddPointToLhs {
            matchController.returnGetCurrentGameState(game15And40)
            matchController.returnGetSideConfig(SideConfig(playerOnLhs = Player.ONE))
        }

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = UiState.Player(name = PLAYER1_NAME, score = "15"),
                            rhsPlayer = UiState.Player(name = PLAYER2_NAME, score = "40"),
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding lhs score with player2 on lhs`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.afterAddPointToLhs {
            matchController.returnGetCurrentGameState(game15And40)
            matchController.returnGetSideConfig(SideConfig(playerOnLhs = Player.TWO))
        }

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = UiState.Player(name = PLAYER2_NAME, score = "15"),
                            rhsPlayer = UiState.Player(name = PLAYER1_NAME, score = "40"),
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding rhs score with player1 on lhs`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.afterAddPointToRhs {
            matchController.returnGetCurrentGameState(game40And15)
            matchController.returnGetSideConfig(SideConfig(playerOnLhs = Player.ONE))
        }

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = UiState.Player(name = PLAYER1_NAME, score = "40"),
                            rhsPlayer = UiState.Player(name = PLAYER2_NAME, score = "15"),
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding rhs score with player2 on lhs`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.afterAddPointToRhs {
            matchController.returnGetCurrentGameState(game40And15)
            matchController.returnGetSideConfig(SideConfig(playerOnLhs = Player.TWO))
        }

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected =
                sampleMatchUiState.copy(
                    game =
                        sampleGameUiState.copy(
                            lhsPlayer = UiState.Player(name = PLAYER2_NAME, score = "40"),
                            rhsPlayer = UiState.Player(name = PLAYER1_NAME, score = "15"),
                        )
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about point scored when lhs scores`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.uiEvents.test {
            matchController.returnAddPointToLhs(
                MatchEvent.PointScored(
                    winnerSide = Side.LHS,
                    withSideSwitch = false
                )
            )
            viewModel.onLhsPressed()
            val expected = MatchUiEvent.PointScored(winner = Side.LHS, withSideSwitch = false)
            assertThat(awaitItem()).isEqualTo(expected)
        }

        viewModel.uiEvents.test {
            matchController.returnAddPointToLhs(MatchEvent.PointScored(winnerSide = Side.LHS, withSideSwitch = true))
            viewModel.onLhsPressed()
            val expected = MatchUiEvent.PointScored(winner = Side.LHS, withSideSwitch = true)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about point scored when rhs scores`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.uiEvents.test {
            matchController.returnAddPointToRhs(
                MatchEvent.PointScored(
                    winnerSide = Side.RHS,
                    withSideSwitch = false
                )
            )
            viewModel.onRhsPressed()
            val expected = MatchUiEvent.PointScored(winner = Side.RHS, withSideSwitch = false)
            assertThat(awaitItem()).isEqualTo(expected)
        }

        viewModel.uiEvents.test {
            matchController.returnAddPointToRhs(MatchEvent.PointScored(winnerSide = Side.RHS, withSideSwitch = true))
            viewModel.onRhsPressed()
            val expected = MatchUiEvent.PointScored(winner = Side.RHS, withSideSwitch = true)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates score when lhs wins game`() = runTest {
        matchController.returnGetCurrentGameState(game40And15)
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        val event = MatchEvent.GameFinished(winnerSide = Side.LHS, lhsGames = 1, rhsGames = 0, withSideSwitch = false)
        matchController.returnAddPointToLhs(event)
        matchController.afterAddPointToLhs {
            matchController.returnGetCurrentGameState(gameLoveAll)
        }

        viewModel.onLhsPressed()

        viewModel.uiState.test {
            val expected = sampleMatchUiState
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates score when rhs wins game`() = runTest {
        matchController.returnGetCurrentGameState(game15And40)
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        val event = MatchEvent.GameFinished(winnerSide = Side.RHS, lhsGames = 0, rhsGames = 1, withSideSwitch = false)
        matchController.returnAddPointToLhs(event)
        matchController.afterAddPointToRhs {
            matchController.returnGetCurrentGameState(gameLoveAll)
        }

        viewModel.onRhsPressed()

        viewModel.uiState.test {
            val expected = sampleMatchUiState
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about game finished when lhs wins game`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.uiEvents.test {
            val event = MatchEvent.GameFinished(winnerSide = Side.LHS, lhsGames = 5, rhsGames = 3, withSideSwitch = false)
            matchController.returnAddPointToLhs(event)
            matchController.afterAddPointToLhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.LHS,
                    lhsScore = "5",
                    rhsScore = "3",
                    withSideSwitch = false,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }

        viewModel.uiEvents.test {
            val event = MatchEvent.GameFinished(winnerSide = Side.LHS, lhsGames = 5, rhsGames = 3, withSideSwitch = true)
            matchController.returnAddPointToLhs(event)
            matchController.afterAddPointToLhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.LHS,
                    lhsScore = "5",
                    rhsScore = "3",
                    withSideSwitch = true,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about game finished when rhs wins game`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.uiEvents.test {
            val event = MatchEvent.GameFinished(winnerSide = Side.RHS, lhsGames = 3, rhsGames = 5, withSideSwitch = false)
            matchController.returnAddPointToRhs(event)
            matchController.afterAddPointToRhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.RHS,
                    lhsScore = "3",
                    rhsScore = "5",
                    withSideSwitch = false,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }

        viewModel.uiEvents.test {
            val event = MatchEvent.GameFinished(winnerSide = Side.RHS, lhsGames = 3, rhsGames = 5, withSideSwitch = true)
            matchController.returnAddPointToRhs(event)
            matchController.afterAddPointToRhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.GameFinished(
                    winner = Side.RHS,
                    lhsScore = "3",
                    rhsScore = "5",
                    withSideSwitch = true,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about set finished when lhs wins set`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.uiEvents.test {
            val event = MatchEvent.SetFinished(winnerSide = Side.LHS, lhsSets = 1, rhsSets = 0, withSideSwitch = false)
            matchController.returnAddPointToLhs(event)
            matchController.afterAddPointToLhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.SetFinished(
                    winner = Side.LHS,
                    lhsScore = "1",
                    rhsScore = "0",
                    withSideSwitch = false,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }

        viewModel.uiEvents.test {
            val event = MatchEvent.SetFinished(winnerSide = Side.LHS, lhsSets = 1, rhsSets = 0, withSideSwitch = true)
            matchController.returnAddPointToLhs(event)
            matchController.afterAddPointToLhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onLhsPressed()

            val expected =
                MatchUiEvent.SetFinished(
                    winner = Side.LHS,
                    lhsScore = "1",
                    rhsScore = "0",
                    withSideSwitch = true,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `notifies about set finished when rhs wins set`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.uiEvents.test {
            val event = MatchEvent.SetFinished(winnerSide = Side.RHS, lhsSets = 0, rhsSets = 1, withSideSwitch = false)
            matchController.returnAddPointToRhs(event)
            matchController.afterAddPointToRhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.SetFinished(
                    winner = Side.RHS,
                    lhsScore = "0",
                    rhsScore = "1",
                    withSideSwitch = false,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }

        viewModel.uiEvents.test {
            val event = MatchEvent.SetFinished(winnerSide = Side.RHS, lhsSets = 0, rhsSets = 1, withSideSwitch = true)
            matchController.returnAddPointToRhs(event)
            matchController.afterAddPointToRhs {
                matchController.returnGetCurrentGameState(gameLoveAll)
            }

            viewModel.onRhsPressed()

            val expected =
                MatchUiEvent.SetFinished(
                    winner = Side.RHS,
                    lhsScore = "0",
                    rhsScore = "1",
                    withSideSwitch = true,
                )
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `updates ui state when adding lhs score in tiebreak`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.afterAddPointToLhs {
            matchController.returnGetCurrentGameState(tiebreak2To5)
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
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.afterAddPointToRhs {
            matchController.returnGetCurrentGameState(tiebreak5To2)
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
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        val event = MatchEvent.MatchFinished(winnerSide = Side.LHS, lhsSets = 3, rhsSets = 1)
        matchController.returnAddPointToLhs(event)
        matchController.afterAddPointToLhs {
            matchController.returnGetCurrentGameState(game40And15)
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
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        val event = MatchEvent.MatchFinished(winnerSide = Side.RHS, lhsSets = 1, rhsSets = 3)
        matchController.returnAddPointToRhs(event)
        matchController.afterAddPointToRhs {
            matchController.returnGetCurrentGameState(game15And40)
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
    fun `does nothing on none match event triggered by lhs`() = runTest {
        matchController.returnGetCurrentGameState(gameLoveAll)
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.returnAddPointToLhs(null)
        matchController.afterAddPointToLhs {
            matchController.returnGetCurrentGameState(game40And15)
        }

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.uiEvents.test {
                viewModel.onLhsPressed()
                expectNoEvents()
            }

            expectNoEvents()
        }
    }

    @Test
    fun `does nothing on none match event triggered by rhs`() = runTest {
        matchController.returnGetCurrentGameState(gameLoveAll)
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        matchController.returnAddPointToRhs(null)
        matchController.afterAddPointToRhs {
            matchController.returnGetCurrentGameState(game15And40)
        }

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.uiEvents.test {
                viewModel.onRhsPressed()
                expectNoEvents()
            }

            expectNoEvents()
        }
    }

    @Test
    fun `stores match details in repository on finished`() = runTest {
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

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
                player1Name = PLAYER1_NAME,
                player2Name = PLAYER2_NAME,
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
        val viewModel = MatchViewModel(matchControllerFactory, matchRepository, savedStateHandle)

        viewModel.navigationEvents.test {
            viewModel.onFinished()
            assertThat(awaitItem()).isEqualTo(MatchNavigationEvent.MatchFinished(matchId = 0))
        }
    }
}
