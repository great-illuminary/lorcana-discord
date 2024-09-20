package eu.codlab.discord.database.models

data class TournamentUserRound(
    val id: Long,
    val tournament: TrackedTournament,
    val user: TournamentUser,
    val roundIndex: String,
    val againstColor1: String?,
    val againstColor2: String?
)