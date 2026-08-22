package dev.jazalewski1.matchpoint.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.PointOutcome
import dev.jazalewski1.matchpoint.feature.match.util.*
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
                val game = matchController.getState().game
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
                        current.copy(
                            lhsPlayer =
                                current.lhsPlayer.copy(
                                    score = game.lhsToString(),
                                    indication = Indication.Minor,
                                ),
                            rhsPlayer = current.rhsPlayer.copy(score = game.rhsToString()),
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
                        current.copy(
                            lhsPlayer =
                                current.lhsPlayer.copy(
                                    score = game.lhsToString(),
                                    indication = null,
                                ),
                            rhsPlayer =
                                current.rhsPlayer.copy(
                                    score = game.rhsToString(),
                                    indication = null,
                                ),
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
                        current.copy(
                            lhsPlayer = current.lhsPlayer.copy(score = game.lhsToString()),
                            rhsPlayer =
                                current.rhsPlayer.copy(
                                    score = game.rhsToString(),
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
                        current.copy(
                            lhsPlayer =
                                current.lhsPlayer.copy(
                                    score = game.lhsToString(),
                                    indication = null,
                                ),
                            rhsPlayer =
                                current.rhsPlayer.copy(
                                    score = game.rhsToString(),
                                    indication = null,
                                ),
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
