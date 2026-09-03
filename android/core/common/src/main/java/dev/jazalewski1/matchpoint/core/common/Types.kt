package dev.jazalewski1.matchpoint.core.common

enum class Player {
    ONE,
    TWO;

    fun opposite() =
        when (this) {
            ONE -> TWO
            TWO -> ONE
        }
}

enum class Side {
    LHS,
    RHS,
}
