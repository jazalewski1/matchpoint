package dev.jazalewski1.matchpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme
import dev.jazalewski1.matchpoint.feature.home.HomeRoute
import dev.jazalewski1.matchpoint.feature.home.homeDestination
import dev.jazalewski1.matchpoint.feature.match.matchDestination
import dev.jazalewski1.matchpoint.feature.match.navigateToMatchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { App() } }
    }
}

@Composable
private fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        homeDestination(onStartClick = { navController.navigateToMatchScreen() })
        matchDestination()
    }
}
