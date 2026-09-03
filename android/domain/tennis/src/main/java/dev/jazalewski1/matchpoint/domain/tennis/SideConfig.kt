package dev.jazalewski1.matchpoint.domain.tennis

import dev.jazalewski1.matchpoint.core.common.Player
import dev.jazalewski1.matchpoint.core.common.Side

class SideConfig(val playerOnLhs: Player = Player.ONE) {
    fun getSide(player: Player) =
        if (playerOnLhs == player) {
            Side.LHS
        } else {
            Side.RHS
        }

    fun getPlayer(side: Side) = if (side == Side.LHS) playerOnLhs else playerOnLhs.opposite()

    fun <T> selectLhs(p1: T, p2: T) = if (playerOnLhs == Player.ONE) p1 else p2

    fun <T> selectRhs(p1: T, p2: T) = if (playerOnLhs == Player.ONE) p2 else p1
}
