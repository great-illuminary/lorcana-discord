package eu.codlab.discord

import dev.kord.common.entity.Snowflake
import eu.codlab.discord.database.models.TrackedTournament
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.commands.SlashCommandEvent
import me.jakejmattson.discordkt.commands.commands

fun tournamentTrack() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("track", "Track a specific melee tournament using its id") {
        execute(
            AnyArg("id", "The tournament id")
        ) {
            val tournamentId = args.first
            val guild = guildOrAuthorFallback

            if (null != currentTournament) {
                respondPublic {
                    field("Tracking", inline = false) {
                        "Tournament already tracked"
                    }
                }
            } else {
                var trackedTournament = LorcanaData.database.tournamentTracker.insert(
                    discordGuild = guild.value.toLong(),
                    discordChannel = channel.id.value.toLong(),
                    tournament = tournamentId
                )

                LorcanaData.database.tournamentTracker.unclose(trackedTournament)

                trackedTournament = LorcanaData.database.tournamentTracker.insert(
                    discordGuild = guild.value.toLong(),
                    discordChannel = channel.id.value.toLong(),
                    tournament = tournamentId
                )

                respondPublic {
                    field("Tracking", inline = false) {
                        "The tournament #${trackedTournament.tournament} is now tracked "
                    }
                }
            }
        }
    }
}

val SlashCommandEvent<*>.guildOrAuthorFallback: Snowflake
    get() {
        return guild?.id ?: channel.id
    }

val SlashCommandEvent<*>.currentTournament: TrackedTournament?
    get() {
        val tournaments = LorcanaData.database.tournamentTracker.selectTrackedTournaments()
        tournaments.forEach { println(it) }
        println("${guildOrAuthorFallback.value.toLong()} - ${channel.id.value.toLong()}")
        return tournaments
            .find {
                it.discordGuild == guildOrAuthorFallback.value.toLong() &&
                        it.discordChannel == channel.id.value.toLong() && !it.closed
            }
    }
