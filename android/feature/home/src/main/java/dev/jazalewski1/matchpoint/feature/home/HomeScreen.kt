package dev.jazalewski1.matchpoint.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jazalewski1.matchpoint.core.ui.common.PrimaryButton
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
internal fun HomeScreen(onStartClick: () -> Unit, onStartDemoClick: () -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(0.5f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_full),
                    contentDescription = "Matchpoint Logo",
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(0.7f).weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                PrimaryButton(
                    text = "Start New Match",
                    onClick = onStartClick,
                    enabled = true,
                    contentDescription = "Starts new match",
                    modifier = Modifier.fillMaxWidth(),
                )
                PrimaryButton( // Temporary for testing purposes. To be removed in the future.
                    text = "Start Demo Match",
                    onClick = onStartDemoClick,
                    enabled = true,
                    contentDescription = "Starts demo match",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme { HomeScreen(onStartClick = {}, onStartDemoClick = {}) }
}
