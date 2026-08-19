package dev.jazalewski1.matchpoint.feature.match

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object MatchRoute

fun NavGraphBuilder.matchDestination() {
    composable<MatchRoute> {
        MatchScreen()
    }
}

fun NavController.navigateToMatchScreen() {
    navigate(route = MatchRoute)
}