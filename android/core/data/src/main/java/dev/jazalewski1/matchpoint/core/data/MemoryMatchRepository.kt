package dev.jazalewski1.matchpoint.core.data

// TODO: Temporary repository until Room is implemented
class MemoryMatchRepository : MatchRepository {
    private val matches: MutableMap<Long, MatchDetails> = mutableMapOf()
    private var nextId: Long = 0

    override fun saveMatch(details: MatchDetails): Long {
        val id = nextId++
        matches += (id to details)
        return id
    }

    override fun getMatch(id: Long): MatchDetails? = matches[id]
}