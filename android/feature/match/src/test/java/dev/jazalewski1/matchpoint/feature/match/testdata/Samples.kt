package dev.jazalewski1.matchpoint.feature.match.testdata

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.Points
import dev.jazalewski1.matchpoint.domain.tennis.SetState

internal val gameLoveAll = GameState.default()
internal val game0And15 = GameState.Regular.Main(Points.LOVE, Points.FIFTEEN)
internal val game15And0 = GameState.Regular.Main(Points.FIFTEEN, Points.LOVE)
internal val game15And40 = GameState.Regular.Main(Points.FIFTEEN, Points.FORTY)
internal val game40And15 = GameState.Regular.Main(Points.FORTY, Points.FIFTEEN)

internal val set0To0 = SetState.default()
internal val set1To0 = SetState(lhs = 1, rhs = 0)
internal val set0To1 = SetState(lhs = 0, rhs = 1)
