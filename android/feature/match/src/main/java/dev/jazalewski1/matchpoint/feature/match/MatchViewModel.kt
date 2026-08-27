package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchState
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
                val game = matchController.getState().game
                MatchUiState(
                    game =
                        GameUiState(
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
                            isTieBreak = game is GameState.TieBreak,
                        )
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
        val matchEvent =
            when (side) {
                Side.LHS -> matchController.addPointToLhs()
                Side.RHS -> matchController.addPointToRhs()
            }
        val matchState = matchController.getState()
        sendEvent(matchState, matchEvent, side)
        updateScores(matchState)
    }

    private fun sendEvent(matchState: MatchState, matchEvent: MatchEvent, side: Side) {
        val event =
            when (matchEvent) {
                is MatchEvent.PointScored -> MatchUiEvent.PointScored(winner = side)
                is MatchEvent.GameWon ->
                    MatchUiEvent.GameFinished(
                        winner = side,
                        lhsScore = matchState.set.lhsGames.toString(),
                        rhsScore = matchState.set.rhsGames.toString(),
                    )
                is MatchEvent.SetWon -> {
                    MatchUiEvent.SetFinished(
                        winner = side,
                        lhsScore = matchState.lhsSets.toString(),
                        rhsScore = matchState.rhsSets.toString(),
                    )
                }
            }
        _uiEvents.tryEmit(event)
    }

    private fun updateScores(matchState: MatchState) {
        _uiState.update { current ->
            val lhsPlayer = current.game.lhsPlayer.copy(score = matchState.game.lhsToString())
            val rhsPlayer = current.game.rhsPlayer.copy(score = matchState.game.rhsToString())
            val gameUiState =
                current.game.copy(
                    lhsPlayer = lhsPlayer,
                    rhsPlayer = rhsPlayer,
                    isTieBreak = matchState.game is GameState.TieBreak,
                )
            current.copy(game = gameUiState)
        }
    }
}
