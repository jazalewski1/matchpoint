package dev.jazalewski1.matchpoint.domain.tennis

enum class Points(val value: Int) {
    LOVE(0) {
        override fun next() = FIFTEEN
    },
    FIFTEEN(15) {
        override fun next() = THIRTY
    },
    THIRTY(30) {
        override fun next() = FORTY
    },
    FORTY(40) {
        override fun next() = null
    };

    abstract fun next(): Points?
}
