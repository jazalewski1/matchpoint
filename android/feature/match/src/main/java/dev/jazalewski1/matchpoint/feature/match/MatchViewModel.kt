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

    fun addPointToLhs() {
        if (isIndicationOngoing()) {
            return
        }
        when (matchController.addPointToLhs()) {
            is PointOutcome.PointScored -> updateFromPointScored(Side.LHS)
            is PointOutcome.GameWon -> updateFromGameWon(Side.LHS)
        }
    }

    fun addPointToRhs() {
        if (isIndicationOngoing()) {
            return
        }
        when (matchController.addPointToRhs()) {
            is PointOutcome.PointScored -> updateFromPointScored(Side.RHS)
            is PointOutcome.GameWon -> updateFromGameWon(Side.RHS)
        }
    }

    private fun updateFromPointScored(side: Side) {
        viewModelScope.launch {
            _uiState.update { current ->
                val game = matchController.getCurrentGame()
                val indication = Indication.Minor
                val lhsPlayer =
                    current.lhsPlayer.copy(
                        score = game.lhsToString(),
                        indication = if (side == Side.LHS) indication else null,
                    )
                val rhsPlayer =
                    current.rhsPlayer.copy(
                        score = game.rhsToString(),
                        indication = if (side == Side.RHS) indication else null,
                    )
                current.copy(lhsPlayer = lhsPlayer, rhsPlayer = rhsPlayer)
            }
            delay(MINOR_INDICATION_DURATION)
            _uiState.update { current ->
                current.copy(
                    lhsPlayer = current.lhsPlayer.copy(indication = null),
                    rhsPlayer = current.rhsPlayer.copy(indication = null),
                )
            }
        }
    }

    private fun updateFromGameWon(side: Side) {
        viewModelScope.launch {
            _uiState.update { current ->
                val indication = Indication.Major
                val lhsPlayer =
                    current.lhsPlayer.copy(indication = if (side == Side.LHS) indication else null)
                val rhsPlayer =
                    current.rhsPlayer.copy(indication = if (side == Side.RHS) indication else null)
                current.copy(lhsPlayer = lhsPlayer, rhsPlayer = rhsPlayer)
            }
            delay(MAJOR_INDICATION_DURATION)
            _uiState.update { current ->
                val game = matchController.getCurrentGame()
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

    private fun isIndicationOngoing(): Boolean {
        val current = _uiState.value
        return current.lhsPlayer.indication != null || current.rhsPlayer.indication != null
    }
}

private enum class Side {
    LHS,
    RHS,
}
