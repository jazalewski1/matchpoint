package dev.jazalewski1.matchpoint.feature.match.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.core.data.MatchDetails
import dev.jazalewski1.matchpoint.core.data.MatchRepository
import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.MatchControllerFactory
import dev.jazalewski1.matchpoint.domain.tennis.MatchEvent
import dev.jazalewski1.matchpoint.domain.tennis.MatchHistory
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
    matchControllerFactory: MatchControllerFactory,
    private val matchRepository: MatchRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<MatchRoute>()
    private val player1Name = route.player1Name
    private val player2Name = route.player2Name
    private val matchController = matchControllerFactory.create(route.numOfSetsToWin)
    private val _uiState =
        MutableStateFlow(
            UiState(
                game =
                    matchController
                        .getCurrentGameState()
                        .toUiState(lhsName = player1Name, rhsName = player2Name)
            )
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
        _uiEvents.tryEmit(matchEvent.toUiEvent())
        updateScores()
    }

    private fun updateScores() {
        val gameState = matchController.getCurrentGameState()
        _uiState.update { current ->
            val lhsPlayer = current.game.lhsPlayer.copy(score = gameState.lhsToString())
            val rhsPlayer = current.game.rhsPlayer.copy(score = gameState.rhsToString())
            val gameUiState =
                current.game.copy(
                    lhsPlayer = lhsPlayer,
                    rhsPlayer = rhsPlayer,
                    isTieBreak = gameState is GameState.TieBreak,
                )
            current.copy(game = gameUiState)
        }
    }
}

private fun MatchEvent.toUiEvent() =
    when (this) {
        is MatchEvent.PointScored -> toUiEvent()
        is MatchEvent.GameWon -> toUiEvent()
        is MatchEvent.SetWon -> toUiEvent()
        is MatchEvent.MatchWon -> toUiEvent()
    }

private fun MatchEvent.PointScored.toUiEvent() = MatchUiEvent.PointScored(winner = this.winnerSide)

private fun MatchEvent.GameWon.toUiEvent() =
    MatchUiEvent.GameFinished(
        winner = this.winnerSide,
        lhsScore = this.lhsGames.toString(),
        rhsScore = this.rhsGames.toString(),
    )

private fun MatchEvent.SetWon.toUiEvent() =
    MatchUiEvent.SetFinished(
        winner = this.winnerSide,
        lhsScore = this.lhsSets.toString(),
        rhsScore = this.rhsSets.toString(),
    )

private fun MatchEvent.MatchWon.toUiEvent() =
    MatchUiEvent.MatchFinished(
        winner = this.winnerSide,
        lhsScore = this.lhsSets.toString(),
        rhsScore = this.rhsSets.toString(),
    )

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
