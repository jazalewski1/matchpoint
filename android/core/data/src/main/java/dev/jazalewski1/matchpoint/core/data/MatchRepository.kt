package dev.jazalewski1.matchpoint.core.data

interface MatchRepository {
    fun saveMatch(details: MatchDetails): Long

    fun getMatch(id: Long): MatchDetails?
}
