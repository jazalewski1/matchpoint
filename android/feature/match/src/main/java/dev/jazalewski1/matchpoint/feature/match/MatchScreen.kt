package dev.jazalewski1.matchpoint.feature.match

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import dev.jazalewski1.matchpoint.core.ui.theme.AppColors
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme
import dev.jazalewski1.matchpoint.core.ui.theme.backgroundLight
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal const val INDICATION_PULSE_HALF_DURATION_MS = 800
internal const val GAME_INDICATION_PULSE_REPS = 5
internal const val GAME_INDICATION_HALF_PULSE_COUNT = GAME_INDICATION_PULSE_REPS * 2
internal const val GAME_INDICATION_TOTAL_DURATION_MS =
    GAME_INDICATION_HALF_PULSE_COUNT * INDICATION_PULSE_HALF_DURATION_MS
internal const val POINT_INDICATION_PULSE_REPS = 3

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
                is MatchUiEvent.PointScored -> indicationState.process(event)
                is MatchUiEvent.GameFinished -> indicationState.process(event)
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
        indicationState.gameIndication?.let {
            GameIndication(
                side = it.side,
                lhsScore = it.lhsScore,
                rhsScore = it.rhsScore,
                onClick = { indicationState.dismissGameIndication() },
            )
        }
    }
}

private data class GameIndication(
    val side: Side,
    val lhsScore: String,
    val rhsScore: String,
)

private class IndicationState(private val scope: CoroutineScope) {
    private var job: Job? = null
    var lhsColor = Animatable(backgroundLight)
        private set

    var rhsColor = Animatable(backgroundLight)
        private set

    var gameIndication by mutableStateOf<GameIndication?>(null)
        private set

    fun process(event: MatchUiEvent.PointScored) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(backgroundLight)
            rhsColor.snapTo(backgroundLight)
            gameIndication = null
            val colorToAnimate = if (event.winner == Side.LHS) lhsColor else rhsColor
            animatePointIndication(colorToAnimate)
        }
    }

    fun process(event: MatchUiEvent.GameFinished) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(backgroundLight)
            rhsColor.snapTo(backgroundLight)
            gameIndication =
                GameIndication(
                    side = event.winner,
                    lhsScore = event.lhsScore,
                    rhsScore = event.rhsScore,
                )
            delay(GAME_INDICATION_TOTAL_DURATION_MS.milliseconds)
            gameIndication = null
        }
    }

    fun dismissGameIndication() {
        if (gameIndication == null) {
            return
        }
        job?.cancel()
        gameIndication = null
    }
}

@Composable
private fun rememberIndicationState(): IndicationState {
    val scope = rememberCoroutineScope()
    return remember { IndicationState(scope) }
}

private suspend fun animatePointIndication(colorToAnimate: Animatable<Color, AnimationVector4D>) {
    val repetitionsWithoutEntryAndExit = POINT_INDICATION_PULSE_REPS - 1
    val easeIn =
        tween<Color>(durationMillis = INDICATION_PULSE_HALF_DURATION_MS, easing = EaseInQuad)
    val easeOut =
        tween<Color>(durationMillis = INDICATION_PULSE_HALF_DURATION_MS, easing = EaseOutQuad)
    colorToAnimate.animateTo(targetValue = AppColors.purpleNormal, animationSpec = easeIn)
    repeat(repetitionsWithoutEntryAndExit) {
        colorToAnimate.animateTo(targetValue = AppColors.purpleLight, animationSpec = easeOut)
        colorToAnimate.animateTo(targetValue = AppColors.purpleNormal, animationSpec = easeIn)
    }
    colorToAnimate.animateTo(targetValue = AppColors.white, animationSpec = easeOut)
}

@Composable
private fun GameIndication(
    side: Side,
    lhsScore: String,
    rhsScore: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val defaultColor = MaterialTheme.colorScheme.primary
    val loudColor = MaterialTheme.colorScheme.tertiary
    val quietColor = Color.Transparent
    val animatedColor by
        rememberInfiniteTransition()
            .animateColor(
                initialValue = quietColor,
                targetValue = loudColor,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                durationMillis = INDICATION_PULSE_HALF_DURATION_MS,
                                easing = EaseOutQuad,
                            ),
                        repeatMode = RepeatMode.Reverse,
                    ),
            )
    val colors =
        if (side == Side.LHS) {
            listOf(animatedColor, quietColor)
        } else {
            listOf(quietColor, animatedColor)
        }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "GAME",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 182.sp,
            )
            Text(
                text = "$lhsScore : $rhsScore",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 92.sp,
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
            lhsScore = "40",
            rhsScore = "15",
            onClick = {},
        )
    }
}
