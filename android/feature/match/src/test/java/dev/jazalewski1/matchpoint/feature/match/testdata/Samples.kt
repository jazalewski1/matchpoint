package dev.jazalewski1.matchpoint.feature.match.testdata

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.Points
import dev.jazalewski1.matchpoint.domain.tennis.SetState

internal val gameLoveAll = GameState.default()
internal val game0And15 = GameState.Regular.Main(Points.LOVE, Points.FIFTEEN)
internal val game15And0 = GameState.Regular.Main(Points.FIFTEEN, Points.LOVE)
internal val game15And40 = GameState.Regular.Main(Points.FIFTEEN, Points.FORTY)
internal val game40And15 = GameState.Regular.Main(Points.FORTY, Points.FIFTEEN)

internal val tiebreak2To5 = GameState.TieBreak(lhs = 2, rhs = 5)
internal val tiebreak5To2 = GameState.TieBreak(lhs = 5, rhs = 2)

internal val set0To0 = SetState.default()
internal val set1To0 = SetState(lhs = 1, rhs = 0)
internal val set0To1 = SetState(lhs = 0, rhs = 1)
