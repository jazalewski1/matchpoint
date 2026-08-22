package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.PointOutcome
import dev.jazalewski1.matchpoint.feature.match.util.toPairOfStrings
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                val (lhsScore, rhsScore) = matchController.getState().game.toPairOfStrings()
                MatchUiState(
                    lhsPlayer =
                        PlayerUiState(
                            name = savedStateHandle.toRoute<MatchRoute>().player1Name,
                            score = lhsScore,
                        ),
                    rhsPlayer =
                        PlayerUiState(
                            name = savedStateHandle.toRoute<MatchRoute>().player2Name,
                            score = rhsScore,
                        ),
                )
            }
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
