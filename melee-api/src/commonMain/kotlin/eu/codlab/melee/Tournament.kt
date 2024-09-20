package eu.codlab.melee

class Tournament(
    private val id: String
) {
    private val matchParser = MatchParser()

    suspend fun matches() = matchParser.matches(id)
}
