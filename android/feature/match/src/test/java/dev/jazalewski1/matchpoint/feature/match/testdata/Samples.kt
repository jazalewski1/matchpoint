package dev.jazalewski1.matchpoint.feature.match.testdata

import dev.jazalewski1.matchpoint.domain.tennis.*

internal val gameLoveAll = GameState.default()
internal val game0And15 = GameState.Regular.Main(Points.LOVE, Points.FIFTEEN)
internal val game15And0 = GameState.Regular.Main(Points.FIFTEEN, Points.LOVE)
internal val game15And40 = GameState.Regular.Main(Points.FIFTEEN, Points.FORTY)
internal val game40And15 = GameState.Regular.Main(Points.FORTY, Points.FIFTEEN)

internal val tiebreak2To5 = GameState.TieBreak(lhsPoints = 2, rhsPoints = 5)
internal val tiebreak5To2 = GameState.TieBreak(lhsPoints = 5, rhsPoints = 2)

internal val set0To0 = SetState.default()
internal val set1To0 = SetState(lhsGames = 1, rhsGames = 0)
internal val set0To1 = SetState(lhsGames = 0, rhsGames = 1)
internal val set6To1 = SetState(lhsGames = 6, rhsGames = 1)
internal val set1To6 = SetState(lhsGames = 1, rhsGames = 6)

internal val match0To0 = MatchState.default()
internal val match1To0 = MatchState(lhsSets = 1, rhsSets = 0)
internal val match0To1 = MatchState(lhsSets = 0, rhsSets = 1)
internal val match3To1 = MatchState(lhsSets = 3, rhsSets = 1)
internal val match1To3 = MatchState(lhsSets = 1, rhsSets = 3)

internal val initialMatch =
    TotalMatchState(
        game = gameLoveAll,
        set = set0To0,
        match = match0To0,
    )
