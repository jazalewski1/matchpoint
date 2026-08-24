package dev.jazalewski1.matchpoint.feature.match

enum class Side {
    LHS,
    RHS,
}

sealed interface IndicationType {
    data object Minor : IndicationType

    data object Major : IndicationType
}

sealed interface MatchUiEvent {
    data class Indication(val type: IndicationType, val side: Side) : MatchUiEvent
}
