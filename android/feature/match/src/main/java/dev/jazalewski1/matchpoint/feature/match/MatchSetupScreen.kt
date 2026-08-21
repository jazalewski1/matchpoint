package dev.jazalewski1.matchpoint.feature.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jazalewski1.matchpoint.core.ui.common.PrimaryButton
import dev.jazalewski1.matchpoint.core.ui.common.PrimaryTextField
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

private const val MAX_NAME_LENGTH = 24

@Composable
internal fun MatchSetupScreen(onStart: (String, String) -> Unit) {
    val player1NameInput = rememberNameInputState()
    val player2NameInput = rememberNameInputState()
    val errors by remember {
        derivedStateOf {
            (player1NameInput.errors + player2NameInput.errors).distinct().map(NameError::toString)
        }
    }
    val isInputValid by remember {
        derivedStateOf { player1NameInput.isValid && player2NameInput.isValid }
    }
    Screen(
        player1Name = player1NameInput.text,
        onPlayer1NameChanged = player1NameInput::change,
        isPlayer1NameValid = player1NameInput.errors.isEmpty(),
        player2Name = player2NameInput.text,
        onPlayer2NameChanged = player2NameInput::change,
        isPlayer2NameValid = player2NameInput.errors.isEmpty(),
        isInputValid = isInputValid,
        onStartClick = { onStart(player1NameInput.text, player2NameInput.text) },
        errors = errors,
    )
}

private class NameInputState {
    var text by mutableStateOf("")
        private set

    var errors by mutableStateOf(listOf<NameError>())
        private set

    var isValid by mutableStateOf(false)
        private set

    init {
        isValid = prepareErrors().isEmpty()
    }

    fun change(newValue: String) {
        text = newValue
        validate()
    }

    private fun validate() {
        errors = prepareErrors()
        isValid = errors.isEmpty()
    }

    private fun prepareErrors(): List<NameError> {
        val newErrors = mutableListOf<NameError>()
        if (text.isBlank()) {
            newErrors.add(NameError.BLANK)
        }
        if (text.length > MAX_NAME_LENGTH) {
            newErrors.add(NameError.TOO_LONG)
        }
        return newErrors
    }
}

private enum class NameError {
    BLANK {
        override fun toString() = "Name cannot be empty."
    },
    TOO_LONG {
        override fun toString() = "Name cannot be longer than $MAX_NAME_LENGTH characters."
    },
}

@Composable private fun rememberNameInputState() = remember { NameInputState() }

@Composable
private fun Screen(
    player1Name: String,
    onPlayer1NameChanged: (String) -> Unit,
    isPlayer1NameValid: Boolean,
    player2Name: String,
    onPlayer2NameChanged: (String) -> Unit,
    isPlayer2NameValid: Boolean,
    isInputValid: Boolean,
    onStartClick: () -> Unit,
    errors: List<String>,
) {
    Scaffold { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()
        ) {
            Header()
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NameInputField(
                    value = player1Name,
                    label = "Player 1",
                    onValueChange = onPlayer1NameChanged,
                    imeAction = ImeAction.Next,
                    isValid = isPlayer1NameValid,
                )
                NameInputField(
                    value = player2Name,
                    label = "Player 2",
                    onValueChange = onPlayer2NameChanged,
                    imeAction = ImeAction.Done,
                    isValid = isPlayer2NameValid,
                )
            }
            if (errors.isNotEmpty()) {
                InputErrorCard(errors)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                PrimaryButton(
                    text = "Start",
                    onClick = onStartClick,
                    enabled = isInputValid,
                    contentDescription = "Starts new match",
                    modifier = Modifier.fillMaxWidth(0.7f),
                )
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
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    )
}

@Composable
private fun NameInputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    imeAction: ImeAction,
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = "${value.length} / $MAX_NAME_LENGTH",
                style = MaterialTheme.typography.labelLarge,
            )
        }
        PrimaryTextField(
            value = value,
            onValueChange = onValueChange,
            isValid = isValid,
            imeAction = imeAction,
            contentDescription = "$label Name",
        )
    }
}

@Composable
private fun InputErrorCard(errors: List<String>) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            for (msg in errors) {
                Text(text = msg, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun ScreenPreviewBase(
    player1Name: String = "Roger",
    isPlayer1NameValid: Boolean = true,
    player2Name: String = "Novak",
    isPlayer2NameValid: Boolean = true,
    isInputValid: Boolean = true,
    errors: List<String> = emptyList(),
) =
    Screen(
        player1Name = player1Name,
        onPlayer1NameChanged = {},
        isPlayer1NameValid = isPlayer1NameValid,
        player2Name = player2Name,
        isPlayer2NameValid = isPlayer2NameValid,
        onPlayer2NameChanged = {},
        isInputValid = isInputValid,
        onStartClick = {},
        errors = errors,
    )

@Preview(showBackground = true, showSystemUi = true, device = DEFAULT)
@Composable
private fun ScreenPreview() {
    AppTheme { ScreenPreviewBase() }
}

@Preview(showBackground = true, showSystemUi = true, device = DEFAULT)
@Composable
private fun ScreenPreviewWithErrors() {
    AppTheme {
        ScreenPreviewBase(
            isPlayer2NameValid = false,
            isInputValid = false,
            errors = listOf("Name cannot contain letters.", "Name is not funny enough."),
        )
    }
}
