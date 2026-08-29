package dev.jazalewski1.matchpoint.feature.match

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable object MatchSetupRoute

@Serializable data class MatchRoute(val player1Name: String, val player2Name: String)

fun NavGraphBuilder.matchSetupDestination(onStart: (String, String) -> Unit) {
    composable<MatchSetupRoute> { MatchSetupScreen(onStart = onStart) }
}

fun NavGraphBuilder.matchDestination() {
    composable<MatchRoute> { MatchScreen(onExit = { TODO() }) }
}

fun NavController.navigateToMatchSetupScreen() {
    navigate(route = MatchSetupRoute)
}

fun NavController.navigateToMatchScreen(player1Name: String, player2Name: String) {
    navigate(route = MatchRoute(player1Name, player2Name)) {
        popUpTo<MatchSetupRoute> { inclusive = true }
    }
}
