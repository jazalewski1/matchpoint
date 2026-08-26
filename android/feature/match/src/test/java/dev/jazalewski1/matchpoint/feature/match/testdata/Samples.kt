package dev.jazalewski1.matchpoint.feature.match.testdata

import dev.jazalewski1.matchpoint.domain.tennis.GameState
import dev.jazalewski1.matchpoint.domain.tennis.Points

internal val gameLoveAll = GameState.default()
internal val game0And15 = GameState.Regular.Main(Points.LOVE, Points.FIFTEEN)
internal val game15And0 = GameState.Regular.Main(Points.FIFTEEN, Points.LOVE)
internal val game15And40 = GameState.Regular.Main(Points.FIFTEEN, Points.FORTY)
internal val game40And15 = GameState.Regular.Main(Points.FORTY, Points.FIFTEEN)
