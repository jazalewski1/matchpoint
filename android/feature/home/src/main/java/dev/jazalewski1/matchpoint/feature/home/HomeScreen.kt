package dev.jazalewski1.matchpoint.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
fun HomeScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(0.5f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_full),
                    contentDescription = "Matchpoint Logo"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Start New Match", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() {
    AppTheme { HomeScreen() }
}
