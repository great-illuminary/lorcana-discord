package eu.codlab.discord.database.models

data class TournamentUserRound(
    val id: Long,
    val tournament: TrackedTournament,
    val user: TournamentUser,
    val roundIndex: String,
    val againstColor1: String?,
    val againstColor2: String?
) {
    val colors: List<String>
        get() {
            return listOfNotNull(againstColor1, againstColor2).sortedBy { it }
        }
}
