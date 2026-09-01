package dev.jazalewski1.matchpoint.feature.match.summary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.ui.common.PrimaryButton
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
internal fun MatchSummaryScreen(
    onReturn: () -> Unit,
    viewModel: MatchSummaryViewModel = hiltViewModel(),
) {
    MatchSummaryScreen(uiState = viewModel.uiState, onReturnClick = onReturn)
}

@Composable
internal fun MatchSummaryScreen(
    uiState: UiState,
    onReturnClick: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Header()
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (uiState) {
                    is LoadedUiState -> TableContainer(uiState = uiState)
                    is ErrorUiState -> ErrorContainer(message = uiState.message)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                PrimaryButton(
                    text = "Return",
                    onClick = onReturnClick,
                    enabled = true,
                    contentDescription = "Returns To Home Screen",
                    modifier = Modifier.fillMaxWidth(0.7f),
                )
            }
        }
    }
}

@Composable
private fun TableContainer(uiState: LoadedUiState) {
    Spacer(Modifier.height(48.dp))
    Table.Grid(uiState)
}

@OptIn(ExperimentalGridApi::class)
private object Table {
    @Composable
    fun Grid(uiState: LoadedUiState) {
        Grid(
            config = {
                column(120.dp)
                repeat(uiState.numOfSets) {
                    column(1.fr)
                }
                repeat(3) {
                    row(60.dp)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(start = 8.dp),
        ) {
            Spacer(Modifier)
            repeat(uiState.numOfSets) { index ->
                val setNumber = index + 1
                val testTag = "TableSetHeader$setNumber"
                SetHeader(index = setNumber, shortened = uiState.numOfSets > 3, testTag = testTag)
            }

            PlayerRow(uiState = uiState.player1, player = Player.ONE)
            PlayerRow(uiState = uiState.player2, player = Player.TWO)
        }
    }

    @Composable
    private fun GridScope.SetHeader(index: Int, shortened: Boolean, testTag: String) {
        val text = if (shortened) "S$index" else "Set $index"
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier.gridItem(alignment = Alignment.Center).testTag(testTag),
        )
    }

    @Composable
    private fun GridScope.PlayerRow(uiState: LoadedUiState.Player, player: Player) {
        val playerIndex = if (player == Player.ONE) 1 else 2
        PlayerNameCell(text = uiState.name, testTag = "TablePlayer${playerIndex}Name")
        for ((index, set) in uiState.sets.withIndex()) {
            val setNumber = index + 1
            val testTag = "TablePlayer${playerIndex}Set$setNumber"
            ScoreCell(uiState = set, testTag = testTag)
        }
    }

    @Composable
    private fun GridScope.PlayerNameCell(text: String, testTag: String) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.gridItem(alignment = Alignment.CenterStart).testTag(testTag),
        )
    }

    @Composable
    private fun ScoreCell(uiState: LoadedUiState.Set, testTag: String) {
        val (games, isWinner, tieBreakPoints) = uiState
        val backgroundColor =
            if (isWinner) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
        Surface(
            color = backgroundColor,
            shape = ShapeDefaults.ExtraLarge,
            modifier = Modifier.fillMaxSize().padding(8.dp).testTag(testTag),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Row {
                    Text(
                        text = games.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                    if (tieBreakPoints != null) {
                        Text(
                            text = tieBreakPoints.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 14.sp,
                            modifier = Modifier.offset(y = -(4.dp)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Text(
        text = "Summary",
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    )
}

@Composable
private fun ErrorContainer(message: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.8f),
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private data object Samples {
    val set1 = LoadedUiState.Set(games = 1, isWinner = false)
    val set2 = LoadedUiState.Set(games = 2, isWinner = false)
    val set3 = LoadedUiState.Set(games = 3, isWinner = false)
    val set6 = LoadedUiState.Set(games = 6, isWinner = true)
    val set6Tb1 = LoadedUiState.Set(games = 6, isWinner = false, tieBreakPoints = 1)
    val set6Tb7 = LoadedUiState.Set(games = 6, isWinner = true, tieBreakPoints = 7)

    val player1 =
        LoadedUiState.Player(
            name = "Federer",
            sets = listOf(set6),
        )
    val player2 =
        LoadedUiState.Player(
            name = "Nadal",
            sets = listOf(set1),
        )
}

@Composable
private fun ScreenPreviewBase(
    uiState: UiState,
    onReturnClick: () -> Unit = {},
) {
    MatchSummaryScreen(
        uiState = uiState,
        onReturnClick = onReturnClick,
    )
}

@Preview
@Composable
private fun PreviewWith1Set() {
    AppTheme {
        ScreenPreviewBase(
            uiState =
                LoadedUiState(
                    player1 = Samples.player1,
                    player2 = Samples.player2,
                    numOfSets = 1,
                )
        )
    }
}

@Preview
@Composable
private fun PreviewWith3Sets() {
    AppTheme {
        ScreenPreviewBase(
            uiState =
                LoadedUiState(
                    player1 =
                        Samples.player1.copy(
                            sets = listOf(Samples.set6, Samples.set6Tb1, Samples.set6)
                        ),
                    player2 =
                        Samples.player2.copy(
                            sets = listOf(Samples.set2, Samples.set6Tb7, Samples.set3)
                        ),
                    numOfSets = 3,
                )
        )
    }
}

@Preview
@Composable
private fun PreviewWith5Sets() {
    AppTheme {
        ScreenPreviewBase(
            uiState =
                LoadedUiState(
                    player1 =
                        Samples.player1.copy(
                            sets =
                                listOf(
                                    Samples.set6,
                                    Samples.set6Tb1,
                                    Samples.set1,
                                    Samples.set6,
                                    Samples.set6,
                                )
                        ),
                    player2 =
                        Samples.player2.copy(
                            sets =
                                listOf(
                                    Samples.set1,
                                    Samples.set6Tb7,
                                    Samples.set6,
                                    Samples.set2,
                                    Samples.set3,
                                )
                        ),
                    numOfSets = 5,
                )
        )
    }
}

@Preview
@Composable
private fun PreviewWithError() {
    AppTheme {
        ScreenPreviewBase(uiState = ErrorUiState(message = "Failed to load match data."))
    }
}
