package eu.codlab.discord

import eu.codlab.discord.transform.InkArg
import eu.codlab.discord.transform.toInkColor
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.lorcana.cards.InkColor
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands

fun tournamentPreValidateRound() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("prevalidate", "Pre-validate an opponent's deck") {
        execute(
            AnyArg("discord", "The discord's @user whom played against the opponent"),
            IntegerArg("round_index", "The round which corresponding to the info, (eg. 1)"),
            InkArg("color_1", "The main color of the opponent's deck"),
            InkArg("color_2", "The 2nd color of the opponent's deck"),
        ) {
            val discord = args.first.toDiscord()
            val roundIndex = args.second
            val color1 = args.third.toInkColor() ?: InkColor.Amber
            val color2 = args.fourth.toInkColor() ?: InkColor.Amber

            if (null == discord) {
                respondPublic {
                    field("Error") { "Invalid username" }
                }

                return@execute
            }

            validateRound(
                roundIndex,
                color1,
                color2,
                discord,
                onNotEnrolledError = { user ->
                    "<@$user> is not enrolled in this tournament"
                },
                onSuccess = { user, color1, color2 ->
                    "<@${user.discordUser}>'s opponent's colors have been set to $color1 / $color2"
                }
            )
        }
    }
}
