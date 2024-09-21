package eu.codlab.discord

import eu.codlab.discord.utils.BotPermissions
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.commands.commands

fun tournamentTrackUserAdmin() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("preregister", "Pre-register a player in a tournament") {
        execute(
            AnyArg("melee", "The user's username on Melee.gg"),
            AnyArg("discord", "The discord's @user")
        ) {
            val melee = args.first
            val actualTag = args.second.toDiscord()

            if (null == actualTag) {
                respondPublic {
                    field("Error") { "Invalid username" }
                }

                return@execute
            }

            trackUser(
                melee,
                actualTag,
                onAlreadyTracked = { _, found ->
                    "<@$found> is already tracked"
                },
                onSuccess = { trackedTournament, user ->
                    "<@$user> has been added to the tournament #${trackedTournament.tournament}"
                }
            )
        }
    }
}

fun String.toDiscord(): Long? {
    val groupNumber = "\\d+".toRegex()
    val discord = groupNumber.find(this)

    return discord?.value?.toLong()
}
