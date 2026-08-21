package dev.jazalewski1.matchpoint.feature.match

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
internal fun MatchScreen(lhsPlayerName: String, rhsPlayerName: String) {
    val context = LocalContext.current
    val scoreState = rememberScoreState()

    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }
    Scaffold { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
            PointContainer(
                playerName = lhsPlayerName,
                score = scoreState.lhsScore,
                onClick = scoreState::increaseLhs,
                contentDescription = "Left Score",
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
            )
            VerticalDivider(thickness = 2.dp)
            PointContainer(
                playerName = rhsPlayerName,
                score = scoreState.rhsScore,
                onClick = scoreState::increaseRhs,
                contentDescription = "Right Score",
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun PointContainer(
    playerName: String,
    score: Int,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clickable(enabled = true, onClick = onClick)
                .semantics(properties = { this.contentDescription = contentDescription }),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = playerName,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        BasicText(
            text = score.toString(),
            style = MaterialTheme.typography.titleLarge,
            autoSize = TextAutoSize.StepBased(maxFontSize = 600.sp),
        )
    }
}

private class ScoreState {
    var lhsScore by mutableIntStateOf(0)
        private set

    var rhsScore by mutableIntStateOf(0)
        private set

    fun increaseLhs() {
        lhsScore += 1
    }

    fun increaseRhs() {
        rhsScore += 1
    }
}

@Composable private fun rememberScoreState() = remember { ScoreState() }

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,orientation=landscape",
)
@Composable
private fun MatchScreenPreview() {
    AppTheme { MatchScreen(lhsPlayerName = "Novak", rhsPlayerName = "Rafael") }
}
