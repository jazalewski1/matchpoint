package dev.jazalewski1.matchpoint.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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

@Composable
private fun PreviewSurface(
    text: String,
    color: Color,
) {
    Surface(
        modifier = Modifier.padding(4.dp).fillMaxWidth(),
        color = color,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = DEFAULT)
@Composable
private fun JustPreview() {
    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PreviewSurface("primary", MaterialTheme.colorScheme.primary)
            PreviewSurface("primaryContainer", MaterialTheme.colorScheme.primaryContainer)
            PreviewSurface("secondary", MaterialTheme.colorScheme.secondary)
            PreviewSurface("secondaryContainer", MaterialTheme.colorScheme.secondaryContainer)
            PreviewSurface("tertiary", MaterialTheme.colorScheme.tertiary)
            PreviewSurface("tertiaryContainer", MaterialTheme.colorScheme.tertiaryContainer)
            PreviewSurface("error", MaterialTheme.colorScheme.error)
            PreviewSurface("errorContainer", MaterialTheme.colorScheme.errorContainer)
        }
    }
}