package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.PointOutcome
import dev.jazalewski1.matchpoint.feature.match.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class MatchViewModel
@Inject
constructor(
    private val matchController: MatchController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            run {
                val game = matchController.getCurrentGame()
                MatchUiState(
                    lhsPlayer =
                        PlayerUiState(
                            name = savedStateHandle.toRoute<MatchRoute>().player1Name,
                            score = game.lhsToString(),
                        ),
                    rhsPlayer =
                        PlayerUiState(
                            name = savedStateHandle.toRoute<MatchRoute>().player2Name,
                            score = game.rhsToString(),
                        ),
                )
            }
        )
    val uiState = _uiState.asStateFlow()
    private val _uiEvents = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
    val uiEvents = _uiEvents.asSharedFlow()

    fun onLhsPressed() {
        when (matchController.addPointToLhs()) {
            is PointOutcome.PointScored -> processPointScored(Side.LHS)
            is PointOutcome.GameWon -> processGameWon(Side.LHS)
        }
    }

    fun onRhsPressed() {
        when (matchController.addPointToRhs()) {
            is PointOutcome.PointScored -> processPointScored(Side.RHS)
            is PointOutcome.GameWon -> processGameWon(Side.RHS)
        }
    }

    fun onIndicationCompletion(event: MatchUiEvent) {
        if (event is MatchUiEvent.Indication && event.type == IndicationType.Major) {
            updateScores()
        }
    }

    private fun processPointScored(side: Side) {
        updateScores()
        _uiEvents.tryEmit(MatchUiEvent.Indication(type = IndicationType.Minor, side = side))
    }

    private fun processGameWon(side: Side) {
        _uiEvents.tryEmit(MatchUiEvent.Indication(type = IndicationType.Major, side = side))
    }

    private fun updateScores() {
        val game = matchController.getCurrentGame()
        _uiState.update { current ->
            current.copy(
                lhsPlayer = current.lhsPlayer.copy(score = game.lhsToString()),
                rhsPlayer = current.rhsPlayer.copy(score = game.rhsToString()),
            )
        }
    }
}
