package eu.codlab.discord.database.models

data class TrackedTournament(
    val id: Long,
    val tournament: String,
    val discordGuild: Long,
    val discordChannel: Long,
    val closed: Boolean
)