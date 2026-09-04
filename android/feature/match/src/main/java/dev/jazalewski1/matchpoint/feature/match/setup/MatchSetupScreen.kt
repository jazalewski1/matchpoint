package dev.jazalewski1.matchpoint.feature.match.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.jazalewski1.matchpoint.core.ui.common.PrimaryButton
import dev.jazalewski1.matchpoint.core.ui.common.PrimaryTextField

private enum class SetOption(val numOfSets: Int, val numOfSetsToWin: Int) {
    BEST_OF_1(numOfSets = 1, numOfSetsToWin = 1),
    BEST_OF_3(numOfSets = 3, numOfSetsToWin = 2),
    BEST_OF_5(numOfSets = 5, numOfSetsToWin = 3);

    fun toLabel() = "Best of $numOfSets"

    fun toWinRequirement() =
        when (this) {
            BEST_OF_1 -> "1 set"
            BEST_OF_3 -> "2 sets"
            BEST_OF_5 -> "3 sets"
        }
}

@Composable
internal fun MatchSetupScreen(onStart: (String, String, Int) -> Unit) {
    val nameInput = rememberNameInputStateHolder()
    var selectedSetOptionIndex by remember { mutableIntStateOf(0) }
    Screen(
        nameFormUiState = nameInput.uiState,
        onPlayer1NameChanged = nameInput::changePlayer1,
        onPlayer2NameChanged = nameInput::changePlayer2,
        selectedSetOptionIndex = selectedSetOptionIndex,
        onStartClick = {
            onStart(
                nameInput.preparePlayer1(),
                nameInput.preparePlayer2(),
                SetOption.entries[selectedSetOptionIndex].numOfSetsToWin,
            )
        },
        onSelectedSetOption = { index -> selectedSetOptionIndex = index },
    )
}

@Composable
private fun Screen(
    nameFormUiState: NameFormUiState,
    onPlayer1NameChanged: (String) -> Unit,
    onPlayer2NameChanged: (String) -> Unit,
    selectedSetOptionIndex: Int,
    onSelectedSetOption: (Int) -> Unit,
    onStartClick: () -> Unit,
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
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NameInputField(
                    value = nameFormUiState.player1.name,
                    label = "Player 1",
                    onValueChange = onPlayer1NameChanged,
                    imeAction = ImeAction.Next,
                    isValid = nameFormUiState.player1.isValid,
                )
                NameInputField(
                    value = nameFormUiState.player2.name,
                    label = "Player 2",
                    onValueChange = onPlayer2NameChanged,
                    imeAction = ImeAction.Done,
                    isValid = nameFormUiState.player2.isValid,
                )
                SetSelectionButton(
                    selectedSetOptionIndex = selectedSetOptionIndex,
                    onClick = onSelectedSetOption,
                )
            }
            if (nameFormUiState.errors.isNotEmpty()) {
                InputErrorCard(nameFormUiState.errors)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                PrimaryButton(
                    text = "Start",
                    onClick = onStartClick,
                    enabled = nameFormUiState.isComplete,
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
private fun SmallLabel(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        content()
    }
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
        SmallLabel {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = label, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "${value.length} / $MAX_NAME_LENGTH",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
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
private fun SetSelectionButton(
    selectedSetOptionIndex: Int,
    onClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SmallLabel {
            Text(text = "Number of sets", style = MaterialTheme.typography.labelLarge)
        }

        val options = SetOption.entries
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    shape =
                        SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size,
                        ),
                    onClick = { onClick(index) },
                    selected = index == selectedSetOptionIndex,
                    label = { Text(option.toLabel()) },
                )
            }
        }

        val winDetails = options[selectedSetOptionIndex].toWinRequirement()
        SmallLabel {
            Text(
                text = "First player to win $winDetails wins the match.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/*
@Composable
private fun ScreenPreviewBase(
    player1Name: NameInputState =
        NameInputState(text = "Roger", isValid = true, errors = emptyList()),
    player2Name: NameInputState =
        NameInputState(text = "Roger", isValid = true, errors = emptyList()),
    selectedSetOptionIndex: Int = 0,
    nameErrors: List<String> = emptyList(),
) =
    Screen(
        player1Name = player1Name,
        onPlayer1NameChanged = {},
        player2Name = player2Name,
        onPlayer2NameChanged = {},
        selectedSetOptionIndex = selectedSetOptionIndex,
        onSelectedSetOption = {},
        onStartClick = {},
        nameErrors = nameErrors,
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
            player2Name = NameInputState(text = "Tennis", isValid = false, errors = emptyList()),
            nameErrors = listOf("Name cannot contain letters.", "Name is not funny enough."),
        )
    }
}

 */
