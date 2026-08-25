package dev.jazalewski1.matchpoint.feature.match

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.EaseInOutCirc
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
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
import dev.jazalewski1.matchpoint.core.ui.theme.backgroundLight
import dev.jazalewski1.matchpoint.core.ui.theme.primaryLight
import dev.jazalewski1.matchpoint.core.ui.theme.secondaryContainerLight
import dev.jazalewski1.matchpoint.core.ui.theme.tertiaryLight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun MatchScreen(viewModel: MatchViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MatchScreen(
        lhsPlayerName = uiState.lhsPlayer.name,
        rhsPlayerName = uiState.rhsPlayer.name,
        lhsScore = uiState.lhsPlayer.score,
        rhsScore = uiState.rhsPlayer.score,
        onLhsClick = viewModel::onLhsPressed,
        onRhsClick = viewModel::onRhsPressed,
        events = viewModel.uiEvents,
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
    events: SharedFlow<MatchUiEvent>,
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

    val indicationState = rememberIndicationState()
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is MatchUiEvent.PointScored -> indicationState.onPointScored(side = event.winner)
                is MatchUiEvent.GameFinished -> indicationState.onGameFinished(side = event.winner)
            }
        }
    }

    Scaffold { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
            PointContainer(
                playerName = lhsPlayerName,
                score = lhsScore,
                onClick = onLhsClick,
                contentDescription = "Left Score",
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
                backgroundColor = indicationState.lhsColor.value,
            )
            VerticalDivider(thickness = 2.dp)
            PointContainer(
                playerName = rhsPlayerName,
                score = rhsScore,
                onClick = onRhsClick,
                contentDescription = "Right Score",
                modifier = Modifier.weight(0.5f).fillMaxHeight(),
                backgroundColor = indicationState.rhsColor.value,
            )
        }
        indicationState.gameIndicationSide?.let { side ->
            GameIndication(
                side = side,
                onClick = { indicationState.dismissGameIndication() },
            )
        }
    }
}

private class IndicationState(private val scope: CoroutineScope) {
    private var job: Job? = null
    var lhsColor = Animatable(backgroundLight)
        private set
    var rhsColor = Animatable(backgroundLight)
        private set
    var gameIndicationSide by mutableStateOf<Side?>(null)
        private set

    fun onPointScored(side: Side) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(backgroundLight)
            rhsColor.snapTo(backgroundLight)
            gameIndicationSide = null
            val colorToAnimate = if (side == Side.LHS) lhsColor else rhsColor
            animatePointIndication(colorToAnimate)
        }
    }

    fun onGameFinished(side: Side) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(backgroundLight)
            rhsColor.snapTo(backgroundLight)
            gameIndicationSide = side
            delay(5.seconds)
            gameIndicationSide = null
        }
    }

    fun dismissGameIndication() {
        if (gameIndicationSide == null) {
            return
        }
        job?.cancel()
        gameIndicationSide = null
    }
}

@Composable
private fun rememberIndicationState(): IndicationState {
    val scope = rememberCoroutineScope()
    return remember { IndicationState(scope) }
}

private suspend fun animatePointIndication(colorToAnimate: Animatable<Color, AnimationVector4D>) {
    val halfIndicationDurationMs = 600
    val iterations = 2
    val tweenSpec =
        tween<Color>(
            durationMillis = halfIndicationDurationMs,
            easing = EaseInOutCubic,
        )
    repeat(iterations) {
        colorToAnimate.animateTo(
            targetValue = secondaryContainerLight,
            animationSpec = tweenSpec,
        )
        delay(halfIndicationDurationMs.milliseconds)
        colorToAnimate.animateTo(
            targetValue = backgroundLight,
            animationSpec = tweenSpec,
        )
    }
}

@Composable
private fun GameIndication(
    side: Side,
    onClick: () -> Unit,
) {
    val defaultColor = primaryLight
    val indicatingColor = tertiaryLight
    val animatedColor by rememberInfiniteTransition()
        .animateColor(
            initialValue = Color.Transparent,
            targetValue = indicatingColor,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = EaseOutCirc),
                repeatMode = RepeatMode.Reverse,
            )
        )
    val colors =
        if (side == Side.LHS) {
            listOf(animatedColor, defaultColor)
        } else {
            listOf(defaultColor, animatedColor)
        }
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            Modifier.fillMaxSize()
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource,
                )
                .background(defaultColor)
                .drawBehind { drawRect(brush = Brush.horizontalGradient(colors)) }
    ) {
        Text(
            text = "GAME",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 182.sp,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun PointContainer(
    playerName: String,
    score: String,
    onClick: () -> Unit,
    contentDescription: String,
    backgroundColor: Color,
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
                .drawBehind { drawRect(backgroundColor) }
                .semantics(properties = { this.contentDescription = contentDescription }),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = playerName,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = score,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
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
) =
    MatchScreen(
        lhsPlayerName = lhsPlayerName,
        rhsPlayerName = rhsPlayerName,
        lhsScore = lhsScore,
        rhsScore = rhsScore,
        onLhsClick = {},
        onRhsClick = {},
        events = MutableSharedFlow(),
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
private fun LhsGameIndicationPreview() {
    AppTheme {
        GameIndication(
            side = Side.LHS,
            onClick = {},
        )
    }
}
