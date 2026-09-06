package dev.jazalewski1.matchpoint.feature.match.main.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.feature.match.main.MatchUiEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal sealed interface IndicationConfig {
    data class Point(val side: Side) : IndicationConfig

    sealed interface Major : IndicationConfig {
        data class Game(
            val side: Side,
            val lhsScore: String,
            val rhsScore: String,
        ) : Major

        data class Set(
            val side: Side,
            val lhsScore: String,
            val rhsScore: String,
        ) : Major

        data class Match(
            val side: Side,
            val lhsScore: String,
            val rhsScore: String,
        ) : Major

        data object SideSwitch : Major
    }
}

internal class IndicationState(private val scope: CoroutineScope) {
    private var job: Job? = null
    var indicationConfig by mutableStateOf<IndicationConfig?>(null)
        private set

    fun process(event: MatchUiEvent.PointScored) {
        job?.cancel()
        job = scope.launch {
            indicationConfig = IndicationConfig.Point(event.winner)
            delay(POINT_INDICATION_TOTAL_DURATION_MS.milliseconds)
            indicationConfig = null
        }
        if (event.withSideSwitch) {
            job?.invokeOnCompletion { startSideSwitchIndication() }
        }
    }

    fun process(event: MatchUiEvent.GameFinished) {
        job?.cancel()
        job = scope.launch {
            indicationConfig =
                IndicationConfig.Major.Game(
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
            indicationConfig =
                IndicationConfig.Major.Set(
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
            indicationConfig =
                IndicationConfig.Major.Match(
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
            indicationConfig = IndicationConfig.Major.SideSwitch
        }
    }
}

@Composable
internal fun rememberIndicationState(): IndicationState {
    val scope = rememberCoroutineScope()
    return remember { IndicationState(scope) }
}
