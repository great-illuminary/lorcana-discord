package eu.codlab.discord

import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.commands.commands

fun tournamentTrackUntrack() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("untrack", "Track yourself in a tournament") {
        execute {
            val tournament = currentTournament

            if (null == currentTournament) {
                respondPublic {
                    field("Tracking", inline = false) {
                        "No Tournament is currently tracked"
                    }
                }
            } else {
                LorcanaData.database.tournamentTracker.untrackAll(
                    discordGuild = guildOrAuthorFallback.value.toLong(),
                    discordChannel = channel.id.value.toLong(),
                )
                respondPublic {
                    field("Tracking", inline = false) {
                        "The tournament has been untracked"
                    }
                }
            }
        }
    }
}
