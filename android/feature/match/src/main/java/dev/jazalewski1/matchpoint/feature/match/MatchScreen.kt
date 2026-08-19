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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
fun MatchScreen() {
    val context = LocalContext.current
    val scoreState = rememberScoreState()
    val lhsPlayerName = "David"
    val rhsPlayerName = "Goliath"

    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Scaffold { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .clickable(
                        enabled = true,
                        onClick = {},
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = lhsPlayerName,
                    style = MaterialTheme.typography.displaySmall,
                )
                BasicText(
                    text = scoreState.lhsScore.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 600.sp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .clickable(
                        enabled = true,
                        onClick = {},
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = rhsPlayerName,
                    style = MaterialTheme.typography.displaySmall,
                )
                BasicText(
                    text = scoreState.rhsScore.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 600.sp),
                )
            }
        }
    }
}

private class ScoreState {
    var lhsScore by mutableIntStateOf(0)
        private set
    var rhsScore by mutableIntStateOf(0)
        private set
}

@Composable
private fun rememberScoreState() = remember { ScoreState() }

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,orientation=landscape",
)
@Composable
private fun MatchScreenPreview() {
    AppTheme { MatchScreen() }
}