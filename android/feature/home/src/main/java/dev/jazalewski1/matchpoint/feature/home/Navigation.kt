package dev.jazalewski1.matchpoint.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable object HomeRoute

fun NavGraphBuilder.homeDestination(onStartClick: () -> Unit, onStartDemoClick: () -> Unit) {
    composable<HomeRoute> {
        HomeScreen(onStartClick = onStartClick, onStartDemoClick = onStartDemoClick)
    }
}

fun NavController.navigateToHomeScreen() {
    navigate(route = HomeRoute) {
        popUpTo<HomeRoute> { inclusive = true }
    }
}
