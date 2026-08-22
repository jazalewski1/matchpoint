package dev.jazalewski1.matchpoint.feature.match

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
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
import kotlin.time.DurationUnit

@Composable
internal fun MatchScreen(viewModel: MatchViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MatchScreen(
        lhsPlayerName = uiState.lhsPlayer.name,
        rhsPlayerName = uiState.rhsPlayer.name,
        lhsScore = uiState.lhsPlayer.score,
        rhsScore = uiState.rhsPlayer.score,
        onLhsClick = viewModel::addLhsScore,
        onRhsClick = viewModel::addRhsScore,
        lhsIndication = uiState.lhsPlayer.indication,
        rhsIndication = uiState.rhsPlayer.indication,
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
    lhsIndication: Indication?,
    rhsIndication: Indication?,
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
                indication = lhsIndication,
            )
            VerticalDivider(thickness = 2.dp)
            PointContainer(
                playerName = rhsPlayerName,
                score = rhsScore,
                onClick = onRhsClick,
                contentDescription = "Right Score",
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
                indication = rhsIndication,
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
    indication: Indication?,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val defaultBackground = MaterialTheme.colorScheme.background
    val minorIndicationBackground = MaterialTheme.colorScheme.secondaryContainer
    val majorIndicationBackground = MaterialTheme.colorScheme.tertiaryContainer

    val backgroundColor = remember { Animatable(defaultBackground) }

    LaunchedEffect(indication) {
        backgroundColor.snapTo(defaultBackground)
        if (indication == null) {
            return@LaunchedEffect
        }
        val targetColor =
            when (indication) {
                Indication.Minor -> minorIndicationBackground
                Indication.Major -> majorIndicationBackground
            }
        val duration = HALF_PULSE_DURATION.toInt(DurationUnit.MILLISECONDS)
        val tweenSpec = tween<Color>(duration, easing = FastOutSlowInEasing)
        while (true) {
            backgroundColor.animateTo(targetColor, tweenSpec)
            backgroundColor.animateTo(defaultBackground, tweenSpec)
        }
    }

    Column(
        modifier =
            modifier
                .clickable(
                    enabled = true,
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource,
                )
                .drawBehind { drawRect(backgroundColor.value) }
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
annotation class HorizontalPreview

@Composable
private fun ScreenPreviewBase(
    lhsPlayerName: String = "Federer",
    rhsPlayerName: String = "Nadal",
    lhsScore: String = "40",
    rhsScore: String = "15",
    lhsIndication: Indication? = null,
    rhsIndication: Indication? = null,
) =
    MatchScreen(
        lhsPlayerName = lhsPlayerName,
        rhsPlayerName = rhsPlayerName,
        lhsScore = lhsScore,
        rhsScore = rhsScore,
        onLhsClick = {},
        onRhsClick = {},
        lhsIndication = lhsIndication,
        rhsIndication = rhsIndication,
    )

@HorizontalPreview
@Composable
private fun Preview() {
    AppTheme {
        ScreenPreviewBase()
    }
}

@HorizontalPreview
@Composable
private fun PreviewWithMinorIndication() {
    AppTheme {
        ScreenPreviewBase(lhsIndication = Indication.Minor)
    }
}

@HorizontalPreview
@Composable
private fun PreviewWithMajorIndication() {
    AppTheme {
        ScreenPreviewBase(lhsIndication = Indication.Major)
    }
}
