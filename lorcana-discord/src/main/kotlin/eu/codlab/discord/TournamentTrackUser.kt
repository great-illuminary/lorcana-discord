package eu.codlab.discord

import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.commands.commands

fun tournamentTrackUser() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("register", "Track yourself in a tournament") {
        execute(
            AnyArg("melee", "Your username on Melee.gg")
        ) {
            val melee = args.first

            val trackedTournament = currentTournament

            if (null == trackedTournament) {
                respondPublic {
                    field("Error") { "No tournament currently tracked" }
                }

                return@execute
            }

            val found = LorcanaData.database.tournamentTracker.selectForUser(
                discordGuild = guildOrAuthorFallback.value.toLong(),
                discordChannel = channel.id.value.toLong(),
                discordUser = author.id.value.toLong()
            ).find { it.tournament.id == trackedTournament.id }

            if (null != found) {
                respondPublic {
                    field("Tracking", inline = false) {
                        "You are already tracked <@${found.discordUser}>"
                    }
                }
            } else {
                LorcanaData.database.tournamentTracker.insert(
                    trackedTournament = trackedTournament,
                    discordUser = author.id.value.toLong(),
                    meleeUserName = melee
                )

                respondPublic {
                    field("Tracking", inline = false) {
                        "You've been added to the tournament #${trackedTournament.tournament}"
                    }
                }
            }
        }
    }
}
