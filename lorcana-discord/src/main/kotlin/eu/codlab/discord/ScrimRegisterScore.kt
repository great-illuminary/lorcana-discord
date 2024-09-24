package eu.codlab.discord

import eu.codlab.discord.database.models.ScrimPlayerResult
import eu.codlab.discord.transform.InkArg
import eu.codlab.discord.transform.toInkColor
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import korlibs.time.DateTime
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands

@Suppress("TooGenericExceptionCaught")
fun scrimRegisterScore() = commands("Scrim", BotPermissions.EVERYONE) {
    globalSlash("scrim", "Register a scrim result") {
        execute(
            AnyArg("opponent", "Your opponent's discord tag, e.g. @Someone"),
            IntegerArg("roundWon", "Number of rounds you won"),
            IntegerArg("roundLost", "Number of rounds you lost"),
        ) {
            val scrimTrack = LorcanaData.database.scrimTracker

            val discord = args.first.toDiscord()
            val roundWon = args.second
            val roundLost = args.third

            val player1 = ScrimPlayerResult(
                discordId = author.id.value.toLong(),
                roundWon = roundWon
            )

            val player2 = ScrimPlayerResult(
                discordId = discord!!,
                roundWon = roundLost
            )

            val pair = if (player1.roundWon >= player2.roundWon) {
                player1 to player2
            } else {
                player2 to player1
            }

            try {
                scrimTrack.insert(
                    guildOrAuthorFallback.value.toLong(),
                    DateTime.now().unixMillisLong / 1000,
                    player1 = pair.first,
                    player2 = pair.second
                )


                respondPublic {
                    field("Scrim") {
                        "The result has been set"
                    }
                }
            } catch (err: Throwable) {
                err.printStackTrace()
                respondPublic {
                    field("Error") { "Couldn't load this tournament info" }
                }
            }
        }
    }
}
