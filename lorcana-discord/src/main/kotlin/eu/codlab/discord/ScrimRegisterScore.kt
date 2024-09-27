package eu.codlab.discord

import eu.codlab.discord.database.models.ScrimPlayerResult
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import korlibs.time.DateTime
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.SlashCommandEvent
import me.jakejmattson.discordkt.commands.commands

fun scrimRegisterScoreOther() = commands("Scrim", BotPermissions.EVERYONE) {
    globalSlash("scrimOthers", "Register a scrim result for 2 players") {
        execute(
            AnyArg("opponent1", "The first player's discord tag, e.g. @Someone1"),
            AnyArg("opponent2", "The second player's discord tag, e.g. @Someone2"),
            IntegerArg("roundWonPlayer1", "Number of rounds won by the first player"),
            IntegerArg("roundWonPlayer2", "Number of rounds won by the second player"),
        ) {
            val player1 = args.first.toDiscord()!!
            val player2 = args.second.toDiscord()!!
            val roundWon1 = args.third
            val roundWon2 = args.fourth

            registerScore(
                player1,
                player2,
                roundWon1,
                roundWon2
            )
        }
    }
}

fun scrimRegisterScore() = commands("Scrim", BotPermissions.EVERYONE) {
    globalSlash("scrim", "Register a scrim result") {
        execute(
            AnyArg("opponent", "Your opponent's discord tag, e.g. @Someone"),
            IntegerArg("roundWon", "Number of rounds you won"),
            IntegerArg("roundLost", "Number of rounds you lost"),
        ) {
            val discord = args.first.toDiscord()!!
            val roundWon = args.second
            val roundLost = args.third

            registerScore(
                author.id.value.toLong(),
                discord,
                roundWon,
                roundLost
            )
        }
    }
}

@Suppress("TooGenericExceptionCaught", "MagicNumber")
private suspend fun SlashCommandEvent<*>.registerScore(
    player1Id: Long,
    player2Id: Long,
    roundWonPlayer1: Int,
    roundWonPlayer2: Int
) {
    val scrimTrack = LorcanaData.database.scrimTracker

    val player1 = ScrimPlayerResult(
        discordId = player1Id,
        roundWon = roundWonPlayer1
    )

    val player2 = ScrimPlayerResult(
        discordId = player2Id,
        roundWon = roundWonPlayer2
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
    } catch (ignored: Throwable) {
        respondPublic {
            field("Error") { "Couldn't load this tournament info" }
        }
    }
}
