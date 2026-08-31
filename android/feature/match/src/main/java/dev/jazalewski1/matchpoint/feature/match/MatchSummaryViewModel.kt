package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.core.data.MatchRepository
import dev.jazalewski1.matchpoint.core.data.Player
import javax.inject.Inject

sealed interface MatchSummaryUiState {
    data class Loaded(
        val player1: PlayerSummaryUiState,
        val player2: PlayerSummaryUiState,
        val numOfSets: Int,
    ) : MatchSummaryUiState

    data class Error(val message: String) : MatchSummaryUiState
}

data class PlayerSummaryUiState(
    val name: String,
    val sets: List<SetUiState>,
)

data class SetUiState(
    val games: Int,
    val isWinner: Boolean,
    val tieBreakPoints: Int? = null,
)

@HiltViewModel
class MatchSummaryViewModel
@Inject
constructor(private val matchRepository: MatchRepository, savedStateHandle: SavedStateHandle) :
    ViewModel() {
    val uiState =
        getUiState(
            matchRepository = matchRepository,
            matchId = savedStateHandle.toRoute<MatchSummaryRoute>().matchId,
        )
}

private fun getUiState(matchRepository: MatchRepository, matchId: Long): MatchSummaryUiState {
    val matchData =
        matchRepository.getMatch(matchId)
            ?: return MatchSummaryUiState.Error(message = "Failed to load match data.")
    val player1 =
        PlayerSummaryUiState(
            name = matchData.player1Name,
            sets =
                matchData.sets.map {
                    SetUiState(
                        games = it.player1Games,
                        isWinner = it.winner == Player.ONE,
                        tieBreakPoints = it.tieBreak?.player1Points,
                    )
                },
        )
    val player2 =
        PlayerSummaryUiState(
            name = matchData.player2Name,
            sets =
                matchData.sets.map {
                    SetUiState(
                        games = it.player2Games,
                        isWinner = it.winner == Player.TWO,
                        tieBreakPoints = it.tieBreak?.player2Points,
                    )
                },
        )
    return MatchSummaryUiState.Loaded(
        player1 = player1,
        player2 = player2,
        numOfSets = matchData.sets.size,
    )
}
