package eu.codlab.discord.database.models

data class TournamentUser(
    val id: Long,
    val tournament: TrackedTournament,
    val meleeUsername: String,
    val discordUser: Long
)
