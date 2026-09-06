package dev.jazalewski1.matchpoint.feature.match.main

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.core.ui.common.contentDesc
import dev.jazalewski1.matchpoint.core.ui.theme.AppColors
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme
import dev.jazalewski1.matchpoint.core.ui.theme.monospaceFontFamily
import dev.jazalewski1.matchpoint.feature.match.main.detail.HorizontalPreview
import dev.jazalewski1.matchpoint.feature.match.main.detail.INDICATION_PULSE_HALF_DURATION_MS
import dev.jazalewski1.matchpoint.feature.match.main.detail.INDICATION_TOTAL_DURATION_MS
import dev.jazalewski1.matchpoint.feature.match.main.detail.Indication
import dev.jazalewski1.matchpoint.feature.match.main.detail.POINT_INDICATION_PULSE_REPS
import dev.jazalewski1.matchpoint.feature.match.main.detail.SwitchIndication
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
internal fun MatchScreen(
    onExit: (Long) -> Unit,
    viewModel: MatchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MatchScreen(
        lhsPlayerName = uiState.game.lhsPlayer.name,
        rhsPlayerName = uiState.game.rhsPlayer.name,
        lhsScore = uiState.game.lhsPlayer.score,
        rhsScore = uiState.game.rhsPlayer.score,
        onLhsClick = viewModel::onLhsPressed,
        onRhsClick = viewModel::onRhsPressed,
        isTieBreak = uiState.game.isTieBreak,
        events = viewModel.uiEvents,
        onMatchFinished = viewModel::onFinished,
        onExit = onExit,
        navigationEvents = viewModel.navigationEvents,
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
    isTieBreak: Boolean,
    events: SharedFlow<MatchUiEvent>,
    onMatchFinished: () -> Unit,
    onExit: (Long) -> Unit,
    navigationEvents: Flow<MatchNavigationEvent>,
) {
    RotateScreenToLandscape()
    HideSystemBars()

    LaunchedEffect(Unit) {
        navigationEvents.collect { event ->
            when (event) {
                is MatchNavigationEvent.Finish -> onExit(event.matchId)
            }
        }
    }

    val indicationState = rememberIndicationState()
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is MatchUiEvent.PointScored -> indicationState.process(event)
                is MatchUiEvent.GameFinished -> indicationState.process(event)
                is MatchUiEvent.SetFinished -> indicationState.process(event)
                is MatchUiEvent.MatchFinished -> indicationState.process(event)
            }
        }
    }

    ScoreContainer(
        lhsPlayerName = lhsPlayerName,
        rhsPlayerName = rhsPlayerName,
        lhsScore = lhsScore,
        rhsScore = rhsScore,
        indicationState = indicationState,
        isTieBreak = isTieBreak,
        onLhsClick = onLhsClick,
        onRhsClick = onRhsClick,
    )

    GameEventIndication(
        indicationState = indicationState,
        onMatchFinished = onMatchFinished,
    )
}

@Composable
private fun RotateScreenToLandscape() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }
}

@Composable
private fun HideSystemBars() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            onDispose {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
}

@Composable
private fun ScoreContainer(
    lhsPlayerName: String,
    rhsPlayerName: String,
    lhsScore: String,
    rhsScore: String,
    indicationState: IndicationState,
    isTieBreak: Boolean,
    onLhsClick: () -> Unit,
    onRhsClick: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        PlayerSection(
            playerName = lhsPlayerName,
            score = lhsScore,
            side = Side.LHS,
            onClick = onLhsClick,
            modifier = Modifier.weight(0.5f).fillMaxHeight().contentDesc("Left Score"),
            backgroundColor = indicationState.lhsColor.value,
        )
        VerticalDivider(thickness = 2.dp)
        PlayerSection(
            playerName = rhsPlayerName,
            score = rhsScore,
            side = Side.RHS,
            onClick = onRhsClick,
            modifier = Modifier.weight(0.5f).fillMaxHeight().contentDesc("Right Score"),
            backgroundColor = indicationState.rhsColor.value,
        )
    }
    if (isTieBreak) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        ) {
            Surface(
                modifier = Modifier.padding(top = 24.dp),
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "TIE-BREAK",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(6.dp),
                )
            }
        }
    }
}

@Composable
private fun PlayerSection(
    playerName: String,
    score: String,
    side: Side,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .clickable(
                    enabled = true,
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource,
                )
                .drawBehind {
                    val colorStops =
                        arrayOf(0.0f to AppColors.Background.bg, 0.8f to backgroundColor)
                    drawRect(Brush.verticalGradient(colorStops = colorStops))
                }
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
            val modifier =
                if (side == Side.LHS) {
                    Modifier.align(Alignment.BottomStart).padding(start = 36.dp)
                } else {
                    Modifier.align(Alignment.BottomEnd).padding(end = 36.dp)
                }
            Text(
                text = playerName,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier,
            )
        }
        Text(
            text = score,
            fontFamily = monospaceFontFamily,
            autoSize = TextAutoSize.StepBased(maxFontSize = 600.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun GameEventIndication(
    indicationState: IndicationState,
    onMatchFinished: () -> Unit,
) {
    indicationState.indicationConfig?.let {
        when (it) {
            is IndicationConfig.Game ->
                Indication(
                    side = it.side,
                    largeText = "GAME",
                    smallText = "${it.lhsScore} : ${it.rhsScore}",
                    onClick = { indicationState.dismissIndication() },
                    backgroundColor = AppColors.Secondary.dark,
                    pulseColor = AppColors.Secondary.light,
                    modifier = Modifier.contentDesc("Game Indication"),
                )

            is IndicationConfig.Set ->
                Indication(
                    side = it.side,
                    largeText = "SET",
                    smallText = "${it.lhsScore} : ${it.rhsScore}",
                    onClick = { indicationState.dismissIndication() },
                    backgroundColor = AppColors.Quaternary.dark,
                    pulseColor = AppColors.Quaternary.light,
                    modifier = Modifier.contentDesc("Set Indication"),
                )

            is IndicationConfig.Match ->
                Indication(
                    side = it.side,
                    largeText = "MATCH",
                    smallText = "${it.lhsScore} : ${it.rhsScore}",
                    onClick = onMatchFinished,
                    backgroundColor = AppColors.Tertiary.dark,
                    pulseColor = AppColors.Tertiary.light,
                    modifier = Modifier.contentDesc("Match Indication"),
                )

            is IndicationConfig.SideSwitch ->
                SwitchIndication(onClick = { indicationState.dismissIndication() })
        }
    }
}

private sealed interface IndicationConfig {
    data class Game(
        val side: Side,
        val lhsScore: String,
        val rhsScore: String,
    ) : IndicationConfig

    data class Set(
        val side: Side,
        val lhsScore: String,
        val rhsScore: String,
    ) : IndicationConfig

    data class Match(
        val side: Side,
        val lhsScore: String,
        val rhsScore: String,
    ) : IndicationConfig

    data object SideSwitch : IndicationConfig
}

private class IndicationState(private val scope: CoroutineScope) {
    private var job: Job? = null
    var lhsColor = Animatable(AppColors.Background.bg)
        private set

    var rhsColor = Animatable(AppColors.Background.bg)
        private set

    var indicationConfig by mutableStateOf<IndicationConfig?>(null)
        private set

    fun process(event: MatchUiEvent.PointScored) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(AppColors.Background.bg)
            rhsColor.snapTo(AppColors.Background.bg)
            indicationConfig = null
            val colorToAnimate = if (event.winner == Side.LHS) lhsColor else rhsColor
            animatePointIndication(colorToAnimate)
        }
        if (event.withSideSwitch) {
            job?.invokeOnCompletion { startSideSwitchIndication() }
        }
    }

    fun process(event: MatchUiEvent.GameFinished) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(AppColors.Background.bg)
            rhsColor.snapTo(AppColors.Background.bg)
            indicationConfig =
                IndicationConfig.Game(
                    side = event.winner,
                    lhsScore = event.lhsScore,
                    rhsScore = event.rhsScore,
                )
            delay(INDICATION_TOTAL_DURATION_MS.milliseconds)
            indicationConfig = null
        }
        if (event.withSideSwitch) {
            job?.invokeOnCompletion { startSideSwitchIndication() }
        }
    }

    fun process(event: MatchUiEvent.SetFinished) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(AppColors.Background.bg)
            rhsColor.snapTo(AppColors.Background.bg)
            indicationConfig =
                IndicationConfig.Set(
                    side = event.winner,
                    lhsScore = event.lhsScore,
                    rhsScore = event.rhsScore,
                )
            delay(INDICATION_TOTAL_DURATION_MS.milliseconds)
            indicationConfig = null
        }
        if (event.withSideSwitch) {
            job?.invokeOnCompletion { startSideSwitchIndication() }
        }
    }

    fun process(event: MatchUiEvent.MatchFinished) {
        job?.cancel()
        job = scope.launch {
            lhsColor.snapTo(AppColors.Background.bg)
            rhsColor.snapTo(AppColors.Background.bg)
            indicationConfig =
                IndicationConfig.Match(
                    side = event.winner,
                    lhsScore = event.lhsScore,
                    rhsScore = event.rhsScore,
                )
        }
    }

    fun dismissIndication() {
        if (indicationConfig == null) {
            return
        }
        job?.cancel()
        indicationConfig = null
    }

    private fun startSideSwitchIndication() {
        job = scope.launch {
            indicationConfig = IndicationConfig.SideSwitch
        }
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
    colorToAnimate.animateTo(targetValue = AppColors.Secondary.mid, animationSpec = easeOut)
    repeat(repetitionsWithoutEntryAndExit) {
        colorToAnimate.animateTo(targetValue = AppColors.Secondary.light, animationSpec = easeIn)
        colorToAnimate.animateTo(targetValue = AppColors.Secondary.mid, animationSpec = easeOut)
    }
    colorToAnimate.animateTo(targetValue = AppColors.Background.bg, animationSpec = easeIn)
}

@Composable
private fun ScreenPreviewBase(
    lhsPlayerName: String = "Federer",
    rhsPlayerName: String = "Nadal",
    lhsScore: String = "40",
    rhsScore: String = "15",
    isTieBreak: Boolean = false,
) =
    MatchScreen(
        lhsPlayerName = lhsPlayerName,
        rhsPlayerName = rhsPlayerName,
        lhsScore = lhsScore,
        rhsScore = rhsScore,
        onLhsClick = {},
        onRhsClick = {},
        isTieBreak = isTieBreak,
        events = MutableSharedFlow(),
        onMatchFinished = {},
        onExit = {},
        navigationEvents = flowOf(),
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
private fun PreviewWithTieBreak() {
    AppTheme {
        ScreenPreviewBase(
            lhsScore = "6",
            rhsScore = "3",
            isTieBreak = true,
        )
    }
}
