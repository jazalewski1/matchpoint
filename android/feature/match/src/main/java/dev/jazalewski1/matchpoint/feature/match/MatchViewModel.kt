package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.feature.match.util.toPairOfStrings
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MatchUiState(
    val lhsPlayerName: String,
    val rhsPlayerName: String,
    val lhsScore: String,
    val rhsScore: String,
)

private fun createMatchUiState(
    lhsPlayerName: String,
    rhsPlayerName: String,
    game: GameState,
): MatchUiState {
    val (lhsScore, rhsScore) = game.toPairOfStrings()
    return MatchUiState(
        lhsPlayerName = lhsPlayerName,
        rhsPlayerName = rhsPlayerName,
        lhsScore = lhsScore,
        rhsScore = rhsScore,
    )
}

@HiltViewModel
class MatchViewModel
@Inject
constructor(
    private val matchController: MatchController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            createMatchUiState(
                lhsPlayerName = savedStateHandle.toRoute<MatchRoute>().player1Name,
                rhsPlayerName = savedStateHandle.toRoute<MatchRoute>().player2Name,
                game = matchController.getState().game,
            )
        )
    val uiState = _uiState.asStateFlow()

    fun addLhsScore() {
        matchController.addLhsScore()
        updateScores()
    }

    fun addRhsScore() {
        matchController.addRhsScore()
        updateScores()
    }

    private fun updateScores() {
        _uiState.update { current ->
            val matchState = matchController.getState()
            val (lhsScore, rhsScore) = matchState.game.toPairOfStrings()
            current.copy(
                lhsScore = lhsScore,
                rhsScore = rhsScore,
            )
        }
    }
}
