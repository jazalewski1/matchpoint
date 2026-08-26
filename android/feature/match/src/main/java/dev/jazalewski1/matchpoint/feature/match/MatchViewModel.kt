package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
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
        process(side = Side.LHS)
    }

    fun onRhsPressed() {
        process(side = Side.RHS)
    }

    private fun process(side: Side) {
        val previousGame = matchController.getCurrentGame()
        val matchEvent =
            when (side) {
                Side.LHS -> matchController.addPointToLhs()
                Side.RHS -> matchController.addPointToRhs()
            }
        updateScores()
        val event =
            when (matchEvent) {
                is MatchEvent.PointScored -> MatchUiEvent.PointScored(winner = side)
                is MatchEvent.GameWon ->
                    MatchUiEvent.GameFinished(
                        winner = side,
                        lhsScore = previousGame.lhsToString(),
                        rhsScore = previousGame.rhsToString(),
                    )
                is MatchEvent.SetWon -> TODO()
            }
        _uiEvents.tryEmit(event)
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
