package dev.jazalewski1.matchpoint.domain.tennis

data class SetState(val lhs: Int, val rhs: Int) {
    companion object {
        fun default() = SetState(lhs = 0, rhs = 0)
    }
}
