package dev.jazalewski1.matchpoint.feature.match

import kotlin.time.Duration.Companion.seconds

internal val HALF_PULSE_DURATION = 1.seconds
internal val PULSE_DURATION = HALF_PULSE_DURATION * 2
internal val MINOR_INDICATION_DURATION = PULSE_DURATION
internal val MAJOR_INDICATION_DURATION = PULSE_DURATION * 3