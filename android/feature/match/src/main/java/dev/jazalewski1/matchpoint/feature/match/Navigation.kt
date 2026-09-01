package dev.jazalewski1.matchpoint.feature.match

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.jazalewski1.matchpoint.feature.match.main.MatchScreen
import dev.jazalewski1.matchpoint.feature.match.setup.MatchSetupScreen
import dev.jazalewski1.matchpoint.feature.match.summary.MatchSummaryScreen
import kotlinx.serialization.Serializable

@Serializable object MatchSetupRoute

@Serializable
data class MatchRoute(val player1Name: String, val player2Name: String, val numOfSetsToWin: Int)

@Serializable data class MatchSummaryRoute(val matchId: Long)

fun NavGraphBuilder.matchSetupDestination(onStart: (String, String, Int) -> Unit) {
    composable<MatchSetupRoute> { MatchSetupScreen(onStart = onStart) }
}

fun NavGraphBuilder.matchDestination(onExit: (Long) -> Unit) {
    composable<MatchRoute> { MatchScreen(onExit = onExit) }
}

fun NavGraphBuilder.matchSummaryDestination(onReturn: () -> Unit) {
    composable<MatchSummaryRoute> { MatchSummaryScreen(onReturn = onReturn) }
}

fun NavController.navigateToMatchSetupScreen() {
    navigate(route = MatchSetupRoute)
}

fun NavController.navigateToMatchScreen(
    player1Name: String,
    player2Name: String,
    numOfSetsToWin: Int,
) {
    navigate(route = MatchRoute(player1Name, player2Name, numOfSetsToWin)) {
        popUpTo<MatchSetupRoute> { inclusive = true }
    }
}

fun NavController.navigateToDemoMatchScreen(
    player1Name: String,
    player2Name: String,
    numOfSetsToWin: Int,
) {
    navigate(route = MatchRoute(player1Name, player2Name, numOfSetsToWin))
}

fun NavController.navigateToMatchSummaryScreen(matchId: Long) {
    navigate(route = MatchSummaryRoute(matchId)) {
        popUpTo<MatchRoute> { inclusive = true }
    }
}
