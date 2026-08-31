package dev.jazalewski1.matchpoint.feature.match.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.data.MatchDetails
import dev.jazalewski1.matchpoint.core.data.MatchRepository
import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchController
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchHistory
import dev.jazalewski1.matchpoint.domain.tennis.TotalMatchState
import dev.jazalewski1.matchpoint.feature.match.MatchRoute
import dev.jazalewski1.matchpoint.feature.match.util.*
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class MatchViewModel
@Inject
constructor(
    private val matchController: MatchController,
    private val matchRepository: MatchRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val player1Name = savedStateHandle.toRoute<MatchRoute>().player1Name
    private val player2Name = savedStateHandle.toRoute<MatchRoute>().player2Name
    private val _uiState =
        MutableStateFlow(
            matchController.getState().toUiState(lhsName = player1Name, rhsName = player2Name)
        )
    val uiState = _uiState.asStateFlow()
    private val _uiEvents = MutableSharedFlow<MatchUiEvent>(extraBufferCapacity = 1)
    val uiEvents = _uiEvents.asSharedFlow()
    private val _navigationEvents = Channel<MatchNavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    fun onLhsPressed() {
        process(side = Side.LHS)
    }

    fun onRhsPressed() {
        process(side = Side.RHS)
    }

    fun onFinished() {
        val matchId =
            matchRepository.saveMatch(
                matchController
                    .getHistory()
                    .toDetails(
                        player1Name = player1Name,
                        player2Name = player2Name,
                    )
            )
        viewModelScope.launch {
            _navigationEvents.send(MatchNavigationEvent.MatchFinished(matchId = matchId))
        }
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

    private fun sendEvent(totalMatchState: TotalMatchState, matchEvent: MatchEvent, side: Side) {
        val event =
            when (matchEvent) {
                is MatchEvent.PointScored -> MatchUiEvent.PointScored(winner = side)
                is MatchEvent.GameWon ->
                    MatchUiEvent.GameFinished(
                        winner = side,
                        lhsScore = totalMatchState.set.lhsGames.toString(),
                        rhsScore = totalMatchState.set.rhsGames.toString(),
                    )
                is MatchEvent.SetWon -> {
                    MatchUiEvent.SetFinished(
                        winner = side,
                        lhsScore = totalMatchState.match.lhsSets.toString(),
                        rhsScore = totalMatchState.match.rhsSets.toString(),
                    )
                }
                is MatchEvent.MatchWon -> {
                    MatchUiEvent.MatchFinished(
                        winner = side,
                        lhsScore = totalMatchState.match.lhsSets.toString(),
                        rhsScore = totalMatchState.match.rhsSets.toString(),
                    )
                }
            }
        _uiEvents.tryEmit(event)
    }

    private fun updateScores(totalMatchState: TotalMatchState) {
        _uiState.update { current ->
            val lhsPlayer = current.game.lhsPlayer.copy(score = totalMatchState.game.lhsToString())
            val rhsPlayer = current.game.rhsPlayer.copy(score = totalMatchState.game.rhsToString())
            val gameUiState =
                current.game.copy(
                    lhsPlayer = lhsPlayer,
                    rhsPlayer = rhsPlayer,
                    isTieBreak = totalMatchState.game is GameState.TieBreak,
                )
            current.copy(game = gameUiState)
        }
    }
}

private fun TotalMatchState.toUiState(lhsName: String, rhsName: String) =
    UiState(game = game.toUiState(lhsName = lhsName, rhsName = rhsName))

private fun GameState.toUiState(lhsName: String, rhsName: String) =
    UiState.Game(
        lhsPlayer = UiState.Player(name = lhsName, score = this.lhsToString()),
        rhsPlayer = UiState.Player(name = rhsName, score = this.rhsToString()),
        isTieBreak = this is GameState.TieBreak,
    )

private fun MatchHistory.toDetails(player1Name: String, player2Name: String) =
    MatchDetails(
        player1Name = player1Name,
        player2Name = player2Name,
        sets = this.sets.map(MatchHistory.Set::toDetails),
    )

private fun MatchHistory.Set.toDetails() =
    MatchDetails.Set(
        player1Games = this.player1Games,
        player2Games = this.player2Games,
        winner = this.winner,
        tieBreak =
            this.tieBreak?.let { tb ->
                MatchDetails.Set.TieBreak(
                    player1Points = tb.player1Points,
                    player2Points = tb.player2Points,
                )
            },
    )
