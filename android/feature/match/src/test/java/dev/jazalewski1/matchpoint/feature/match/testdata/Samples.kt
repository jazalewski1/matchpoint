package dev.jazalewski1.matchpoint.feature.match.testdata

import dev.jazalewski1.matchpoint.domain.tennis.*

internal val gameLoveAll = GameState.default()
internal val game15And40 = GameState.Regular.Main(Points.FIFTEEN, Points.FORTY)
internal val game40And15 = GameState.Regular.Main(Points.FORTY, Points.FIFTEEN)

internal val tiebreak2To5 = GameState.TieBreak(lhsPoints = 2, rhsPoints = 5)
internal val tiebreak5To2 = GameState.TieBreak(lhsPoints = 5, rhsPoints = 2)

internal val initialMatch = TotalMatchState(game = gameLoveAll)
