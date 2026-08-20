package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
            Header()
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NameInputField(
                    value = player1Name,
                    label = "Player 1",
                    onValueChange = onPlayer1NameChanged,
                    imeAction = ImeAction.Next,
                )
                NameInputField(
                    value = player2Name,
                    label = "Player 2",
                    onValueChange = onPlayer2NameChanged,
                    imeAction = ImeAction.Done,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                StartButton(onClick = {}, enabled = true)
            }
        }
    }
}

@Composable
private fun Header() {
    Text(
        text = "New Match",
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}

@Composable
private fun NameInputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(8.dp),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics(properties = {
                    this.contentDescription = "$label Name"
                }),
        )
    }
}

@Composable
private fun StartButton(onClick: () -> Unit, enabled: Boolean) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .semantics(properties = {
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