package eu.codlab.discord

import eu.codlab.discord.database.models.TrackedTournament
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.commands.SlashCommandEvent
import me.jakejmattson.discordkt.commands.commands

fun tournamentTrackUser() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("register", "Track yourself in a tournament") {
        execute(
            AnyArg("melee", "Your username on Melee.gg")
        ) {
            val melee = args.first

            trackUser(
                melee,
                author.id.value.toLong(),
                onAlreadyTracked = { _, found ->
                    "You are already tracked <@$found>"
                },
                onSuccess = { trackedTournament, _ ->
                    "You've been added to the tournament #${trackedTournament.tournament}"
                }
            )
        }
    }
}

suspend fun SlashCommandEvent<*>.trackUser(
    melee: String,
    user: Long,
    onAlreadyTracked: (TrackedTournament, user: Long) -> String,
    onSuccess: (TrackedTournament, user: Long) -> String
) {
    val trackedTournament = currentTournament

    if (null == trackedTournament) {
        respondPublic {
            field("Error") { "No tournament currently tracked" }
        }

        return
    }

    val found = LorcanaData.database.tournamentTracker.selectForUser(
        discordGuild = guildOrAuthorFallback.value.toLong(),
        discordChannel = channel.id.value.toLong(),
        discordUser = user
    ).find { it.tournament.id == trackedTournament.id }

    if (null != found) {
        respondPublic {
            field("Tracking", inline = false) {
                onAlreadyTracked(trackedTournament, user)
            }
        }
    } else {
        LorcanaData.database.tournamentTracker.insert(
            trackedTournament = trackedTournament,
            discordUser = user,
            meleeUserName = melee
        )

        respondPublic {
            field("Tracking", inline = false) {
                onSuccess(trackedTournament, user)
            }
        }
    }
}
