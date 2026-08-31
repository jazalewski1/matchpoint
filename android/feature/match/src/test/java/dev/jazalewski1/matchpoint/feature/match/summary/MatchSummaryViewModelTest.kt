package dev.jazalewski1.matchpoint.feature.match.summary

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.jazalewski1.matchpoint.core.data.MatchDetails
import dev.jazalewski1.matchpoint.core.data.MemoryMatchRepository
import dev.jazalewski1.matchpoint.core.data.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

private const val PLAYER1_NAME = "Player1"
private const val PLAYER2_NAME = "Player2"

private val sampleMatchDetails =
    MatchDetails(
        player1Name = PLAYER1_NAME,
        player2Name = PLAYER2_NAME,
        sets =
            listOf(
                MatchDetails.Set(
                    player1Games = 6,
                    player2Games = 1,
                    winner = Player.ONE,
                    tieBreak = null,
                ),
                MatchDetails.Set(
                    player1Games = 2,
                    player2Games = 6,
                    winner = Player.TWO,
                    tieBreak = null,
                ),
                MatchDetails.Set(
                    player1Games = 6,
                    player2Games = 6,
                    winner = Player.ONE,
                    tieBreak =
                        MatchDetails.Set.TieBreak(
                            player1Points = 7,
                            player2Points = 3,
                        ),
                ),
                MatchDetails.Set(
                    player1Games = 6,
                    player2Games = 6,
                    winner = Player.TWO,
                    tieBreak =
                        MatchDetails.Set.TieBreak(
                            player1Points = 4,
                            player2Points = 7,
                        ),
                ),
            ),
    )

@RunWith(AndroidJUnit4::class)
class MatchSummaryViewModelTest {
    @Test
    fun `loads ui state with match data`() {
        val matchRepository = MemoryMatchRepository()
        matchRepository.saveMatch(sampleMatchDetails)
        val matchId: Long = 0
        val savedStateHandle = SavedStateHandle(mapOf("matchId" to matchId))
        val viewModel =
            MatchSummaryViewModel(
                matchRepository = matchRepository,
                savedStateHandle = savedStateHandle,
            )

        val expected =
            MatchSummaryUiState.Loaded(
                player1 =
                    PlayerUiState(
                        name = PLAYER1_NAME,
                        sets =
                            listOf(
                                SetUiState(games = 6, isWinner = true, tieBreakPoints = null),
                                SetUiState(games = 2, isWinner = false, tieBreakPoints = null),
                                SetUiState(games = 6, isWinner = true, tieBreakPoints = 7),
                                SetUiState(games = 6, isWinner = false, tieBreakPoints = 4),
                            ),
                    ),
                player2 =
                    PlayerUiState(
                        name = PLAYER2_NAME,
                        sets =
                            listOf(
                                SetUiState(games = 1, isWinner = false, tieBreakPoints = null),
                                SetUiState(games = 6, isWinner = true, tieBreakPoints = null),
                                SetUiState(games = 6, isWinner = false, tieBreakPoints = 3),
                                SetUiState(games = 6, isWinner = true, tieBreakPoints = 7),
                            ),
                    ),
                numOfSets = 4,
            )

        assertThat(viewModel.uiState).isEqualTo(expected)
    }

    @Test
    fun `loads ui state with error when match id is not found`() {
        val matchRepository = MemoryMatchRepository()
        matchRepository.saveMatch(sampleMatchDetails)
        val invalidMatchId: Long = 99
        val savedStateHandle = SavedStateHandle(mapOf("matchId" to invalidMatchId))
        val viewModel =
            MatchSummaryViewModel(
                matchRepository = matchRepository,
                savedStateHandle = savedStateHandle,
            )

        assertThat(viewModel.uiState)
            .isEqualTo(MatchSummaryUiState.Error(message = "Failed to load match data."))
    }
}
