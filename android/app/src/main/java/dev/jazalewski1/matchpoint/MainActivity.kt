package dev.jazalewski1.matchpoint

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme
import dev.jazalewski1.matchpoint.feature.home.HomeRoute
import dev.jazalewski1.matchpoint.feature.home.homeDestination
import dev.jazalewski1.matchpoint.feature.home.navigateToHomeScreen
import dev.jazalewski1.matchpoint.feature.match.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { App() } }
    }
}

@HiltAndroidApp class Application : Application()

@Composable
private fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        homeDestination(
            onStartClick = { navController.navigateToMatchSetupScreen() },
            onStartDemoClick = {
                navController.navigateToMatchScreen(player1Name = "Nadal", player2Name = "Federer")
            },
        )

        matchSetupDestination(
            onStart = { player1Name, player2Name ->
                navController.navigateToMatchScreen(player1Name, player2Name)
            }
        )

        matchDestination(
            onExit = { matchId -> Log.i("dev", "onExit, id=$matchId"); navController.navigateToMatchSummaryScreen(matchId = matchId) }
        )

        matchSummaryDestination(onReturn = { navController.navigateToHomeScreen() })
    }
}
