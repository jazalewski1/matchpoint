package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.PointOutcome
import dev.jazalewski1.matchpoint.feature.match.util.toPairOfStrings
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface Indication {
    data object Minor : Indication

    data object Major : Indication
}

data class PlayerUiState(
    val name: String,
    val score: String,
    val indication: Indication? = null,
)

data class MatchUiState(
    val lhsPlayer: PlayerUiState,
    val rhsPlayer: PlayerUiState,
)

private fun createMatchUiState(
    lhsPlayerName: String,
    rhsPlayerName: String,
    game: GameState,
): MatchUiState {
    val (lhsScore, rhsScore) = game.toPairOfStrings()
    return MatchUiState(
        lhsPlayer =
            PlayerUiState(
                name = lhsPlayerName,
                score = lhsScore,
            ),
        rhsPlayer =
            PlayerUiState(
                name = rhsPlayerName,
                score = rhsScore,
            ),
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
        if (isIndicationOngoing()) {
            return
        }
        val outcome = matchController.addLhsScore()
        viewModelScope.launch {
            when (outcome) {
                is PointOutcome.PointScored -> {
                    _uiState.update { current ->
                        val game = matchController.getState().game
                        val (lhsScore, rhsScore) = game.toPairOfStrings()
                        current.copy(
                            lhsPlayer =
                                current.lhsPlayer.copy(
                                    score = lhsScore,
                                    indication = Indication.Minor,
                                ),
                            rhsPlayer = current.rhsPlayer.copy(score = rhsScore),
                        )
                    }
                    delay(MINOR_INDICATION_DURATION)
                    _uiState.update { current ->
                        current.copy(lhsPlayer = current.lhsPlayer.copy(indication = null))
                    }
                }
                is PointOutcome.GameWon -> {
                    _uiState.update { current ->
                        current.copy(
                            lhsPlayer = current.lhsPlayer.copy(indication = Indication.Major)
                        )
                    }
                    delay(MAJOR_INDICATION_DURATION)
                    _uiState.update { current ->
                        val game = matchController.getState().game
                        val (lhsScore, rhsScore) = game.toPairOfStrings()
                        current.copy(
                            lhsPlayer = current.lhsPlayer.copy(score = lhsScore, indication = null),
                            rhsPlayer = current.rhsPlayer.copy(score = rhsScore, indication = null),
                        )
                    }
                }
            }
        }
    }

    fun addRhsScore() {
        if (isIndicationOngoing()) {
            return
        }
        val outcome = matchController.addRhsScore()
        viewModelScope.launch {
            when (outcome) {
                is PointOutcome.PointScored -> {
                    _uiState.update { current ->
                        val game = matchController.getState().game
                        val (lhsScore, rhsScore) = game.toPairOfStrings()
                        current.copy(
                            lhsPlayer = current.lhsPlayer.copy(score = lhsScore),
                            rhsPlayer =
                                current.rhsPlayer.copy(
                                    score = rhsScore,
                                    indication = Indication.Minor,
                                ),
                        )
                    }
                    delay(MINOR_INDICATION_DURATION)
                    _uiState.update { current ->
                        current.copy(rhsPlayer = current.rhsPlayer.copy(indication = null))
                    }
                }
                is PointOutcome.GameWon -> {
                    _uiState.update { current ->
                        current.copy(
                            rhsPlayer = current.rhsPlayer.copy(indication = Indication.Major)
                        )
                    }
                    delay(MAJOR_INDICATION_DURATION)
                    _uiState.update { current ->
                        val game = matchController.getState().game
                        val (lhsScore, rhsScore) = game.toPairOfStrings()
                        current.copy(
                            lhsPlayer = current.lhsPlayer.copy(score = lhsScore, indication = null),
                            rhsPlayer = current.rhsPlayer.copy(score = rhsScore, indication = null),
                        )
                    }
                }
            }
        }
    }

    private fun isIndicationOngoing(): Boolean {
        val current = _uiState.value
        return current.lhsPlayer.indication != null || current.rhsPlayer.indication != null
    }
}
