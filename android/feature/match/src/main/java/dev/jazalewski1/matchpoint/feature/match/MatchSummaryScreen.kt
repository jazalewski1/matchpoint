package dev.jazalewski1.matchpoint.feature.match

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
import dev.jazalewski1.matchpoint.core.ui.common.PrimaryButton
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

internal data class MatchDetails(
    val player1Name: String,
    val player2Name: String,
    val sets: List<Set>,
) {
    data class Set(
        val player1Games: Int,
        val player2Games: Int,
        val winner: Player,
        val tieBreak: TieBreak? = null,
    ) {
        data class TieBreak(val player1Points: Int, val player2Points: Int)
    }
}

internal enum class Player {
    ONE,
    TWO,
}

private typealias SetWinner = Player

private data class PlayerSetDetails(
    val games: Int,
    val winner: Boolean,
    val tieBreakPoints: Int? = null,
)

private fun MatchDetails.Set.getPlayerSetDetails(player: Player): PlayerSetDetails {
    val (games, tbPoints) =
        if (player == Player.ONE) {
            Pair(this.player1Games, this.tieBreak?.player1Points)
        } else {
            Pair(this.player2Games, this.tieBreak?.player2Points)
        }
    return PlayerSetDetails(
        games = games,
        winner = this.winner == player,
        tieBreakPoints = tbPoints,
    )
}

@Composable
internal fun MatchSummaryScreen(
    details: MatchDetails,
    onReturnClick: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Header()
            Spacer(Modifier.height(48.dp))
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                Table.Grid(details)
            }
            Row( // TODO: copied from setup screen, move to common
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

private object Table {
    @OptIn(ExperimentalGridApi::class)
    @Composable
    fun Grid(details: MatchDetails) {
        Grid(
            config = {
                column(120.dp)
                repeat(details.sets.size) {
                    column(1.fr)
                }
                repeat(3) {
                    row(60.dp)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(start = 8.dp),
        ) {
            Spacer(Modifier)
            repeat(details.sets.size) { index ->
                val setNumber = index + 1
                val testTag = "TableSetHeader$setNumber"
                SetHeader(index = setNumber, shortened = details.sets.size > 3, testTag = testTag)
            }

            PlayerNameCell(text = details.player1Name, testTag = "TablePlayer1Name")
            for ((index, set) in details.sets.withIndex()) {
                val setNumber = index + 1
                val testTag = "TablePlayer1Set$setNumber"
                ScoreCell(details = set.getPlayerSetDetails(Player.ONE), testTag = testTag)
            }

            PlayerNameCell(text = details.player2Name, testTag = "TablePlayer2Name")
            for ((index, set) in details.sets.withIndex()) {
                val setNumber = index + 1
                val testTag = "TablePlayer2Set$setNumber"
                ScoreCell(details = set.getPlayerSetDetails(Player.TWO), testTag = testTag)
            }
        }
    }

    @OptIn(ExperimentalGridApi::class)
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

    @OptIn(ExperimentalGridApi::class)
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
    private fun ScoreCell(details: PlayerSetDetails, testTag: String) {
        val (games, winner, tieBreakPoints) = details
        val backgroundColor =
            if (winner) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
        Surface(
            color = backgroundColor,
            shape = ShapeDefaults.ExtraLarge,
            modifier = Modifier.fillMaxSize().padding(8.dp).testTag(testTag),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                if (tieBreakPoints != null) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = games.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = tieBreakPoints.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 14.sp,
                            modifier = Modifier.offset(y = -(4.dp)),
                        )
                    }
                } else {
                    Text(
                        text = games.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun Header() { // TODO: copied from setup screen, move to common
    Text(
        text = "Summary",
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    )
}

private val sampleMatchDetails =
    MatchDetails(
        player1Name = "Federer",
        player2Name = "Nadal",
        sets = listOf(MatchDetails.Set(player1Games = 6, player2Games = 2, winner = SetWinner.ONE)),
    )

@Composable
private fun ScreenPreviewBase(
    details: MatchDetails = sampleMatchDetails,
    onReturnClick: () -> Unit = {},
) {
    MatchSummaryScreen(
        details = details,
        onReturnClick = onReturnClick,
    )
}

@Preview
@Composable
private fun PreviewWith1Set() {
    AppTheme {
        ScreenPreviewBase(details = sampleMatchDetails)
    }
}

@Preview
@Composable
private fun PreviewWith3Sets() {
    AppTheme {
        ScreenPreviewBase(
            details =
                sampleMatchDetails.copy(
                    sets =
                        listOf(
                            MatchDetails.Set(6, 2, SetWinner.ONE),
                            MatchDetails.Set(6, 6, SetWinner.TWO, MatchDetails.Set.TieBreak(2, 7)),
                            MatchDetails.Set(6, 0, SetWinner.ONE),
                        )
                )
        )
    }
}

@Preview
@Composable
private fun PreviewWith5Sets() {
    AppTheme {
        ScreenPreviewBase(
            details =
                sampleMatchDetails.copy(
                    sets =
                        listOf(
                            MatchDetails.Set(6, 2, SetWinner.ONE),
                            MatchDetails.Set(4, 6, SetWinner.TWO),
                            MatchDetails.Set(6, 0, SetWinner.ONE),
                            MatchDetails.Set(6, 6, SetWinner.TWO, MatchDetails.Set.TieBreak(4, 7)),
                            MatchDetails.Set(6, 4, SetWinner.ONE),
                        )
                )
        )
    }
}
