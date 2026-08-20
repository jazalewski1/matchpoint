package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
fun MatchSetupScreen() { // TODO: make internal
    val player1NameInput = rememberNameInputState()
    val player2NameInput = rememberNameInputState()
    Screen(
        player1Name = player1NameInput.text,
        onPlayer1NameChanged = player1NameInput::change,
        player2Name = player2NameInput.text,
        onPlayer2NameChanged = player2NameInput::change,
    )
}

private class NameInputState {
    var text by mutableStateOf("")
        private set
    var errors by mutableStateOf(listOf<String>())
        private set

    fun change(newValue: String) {
        text = newValue
    }
}

@Composable
private fun rememberNameInputState() = remember { NameInputState() }

@Composable
private fun Screen(
    player1Name: String,
    onPlayer1NameChanged: (String) -> Unit,
    player2Name: String,
    onPlayer2NameChanged: (String) -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
        ) {
            Text(
                text = "New Match",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            )
            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                TextField(
                    value = player1Name,
                    onValueChange = onPlayer1NameChanged,
                    modifier = Modifier.fillMaxWidth().semantics(properties = {
                        this.contentDescription = "Player 1 Name"
                    }),
                    label = { Text("Player 1", style = MaterialTheme.typography.labelMedium) },
                )
                TextField(
                    value = player2Name,
                    onValueChange = onPlayer2NameChanged,
                    modifier = Modifier.fillMaxWidth().semantics(properties = {
                        this.contentDescription = "Player 2 Name"
                    }),
                    label = { Text("Player 2", style = MaterialTheme.typography.labelMedium) },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = {},
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(0.7f).semantics(properties = {
                        this.contentDescription = "Starts new match"
                    }),
                ) {
                    Text(
                        text = "Start",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = DEFAULT)
@Composable
private fun ScreenPreview() {
    AppTheme {
        Screen(
            player1Name = "Roger",
            onPlayer1NameChanged = {},
            player2Name = "Novak",
            onPlayer2NameChanged = {},
        )
    }
}