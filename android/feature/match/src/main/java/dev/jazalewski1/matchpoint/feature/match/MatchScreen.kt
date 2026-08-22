package dev.jazalewski1.matchpoint.feature.match

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
internal fun MatchScreen(viewModel: MatchViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MatchScreen(
        lhsPlayerName = uiState.lhsPlayerName,
        rhsPlayerName = uiState.rhsPlayerName,
        lhsScore = uiState.lhsScore,
        rhsScore = uiState.rhsScore,
        onLhsClick = viewModel::addLhsScore,
        onRhsClick = viewModel::addRhsScore,
    )
}

@Composable
internal fun MatchScreen(
    lhsPlayerName: String,
    rhsPlayerName: String,
    lhsScore: String,
    rhsScore: String,
    onLhsClick: () -> Unit,
    onRhsClick: () -> Unit,
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

    Scaffold { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
            PointContainer(
                playerName = lhsPlayerName,
                score = lhsScore,
                onClick = onLhsClick,
                contentDescription = "Left Score",
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
            )
            VerticalDivider(thickness = 2.dp)
            PointContainer(
                playerName = rhsPlayerName,
                score = rhsScore,
                onClick = onRhsClick,
                contentDescription = "Right Score",
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun PointContainer(
    playerName: String,
    score: String,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier =
            modifier
                .clickable(
                    enabled = true,
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource,
                )
                .semantics(properties = { this.contentDescription = contentDescription }),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = playerName,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
        )
        BasicText(
            text = score,
            style = MaterialTheme.typography.titleLarge,
            autoSize = TextAutoSize.StepBased(maxFontSize = 600.sp),
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,orientation=landscape",
)
@Composable
private fun Preview() {
    AppTheme {
        MatchScreen(
            lhsPlayerName = "Federer",
            rhsPlayerName = "Nadal",
            lhsScore = "40",
            rhsScore = "15",
            onLhsClick = {},
            onRhsClick = {},
        )
    }
}
