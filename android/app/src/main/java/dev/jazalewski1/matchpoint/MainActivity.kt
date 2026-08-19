package dev.jazalewski1.matchpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme
import dev.jazalewski1.matchpoint.feature.home.HomeScreen
import dev.jazalewski1.matchpoint.feature.match.MatchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContent { AppTheme { HomeScreen() } }
        setContent { AppTheme { MatchScreen() } }
    }
}
