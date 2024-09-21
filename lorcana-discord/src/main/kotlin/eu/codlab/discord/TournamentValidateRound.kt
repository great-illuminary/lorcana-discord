package eu.codlab.discord

import eu.codlab.discord.database.models.TournamentUser
import eu.codlab.discord.transform.InkArg
import eu.codlab.discord.transform.toInkColor
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import eu.codlab.lorcana.cards.InkColor
import eu.codlab.melee.Tournament
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.SlashCommandEvent
import me.jakejmattson.discordkt.commands.commands

fun tournamentValidateRound() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("validate", "Validate your opponent's deck") {
        execute(
            IntegerArg("round_index", "The round you register the info for, (eg. 1)"),
            InkArg("color_1", "The main color of your opponent's deck"),
            InkArg("color_2", "The 2nd color of your opponent's deck"),
        ) {
            val roundIndex = args.first
            val color1 = args.second.toInkColor() ?: InkColor.Amber
            val color2 = args.third.toInkColor() ?: InkColor.Amber

            validateRound(
                roundIndex,
                color1,
                color2,
                channel.id.value.toLong(),
                onNotEnrolledError = {
                    "You are not enrolled in this tournament"
                },
                onSuccess = { _, color1, color2 ->
                    "You set your opponent's colors to $color1 / $color2"
                }
            )
        }
    }
}

suspend fun SlashCommandEvent<*>.validateRound(
    roundIndex: Int,
    color1: InkColor,
    color2: InkColor,
    user: Long,
    onNotEnrolledError: (Long) -> String,
    onSuccess: (TournamentUser, InkColor, InkColor) -> String
) {
    val trackedTournament = currentTournament

    if (null == trackedTournament) {
        respondPublic {
            field("Error") { "No tournament currently tracked" }
        }

        return
    }

    val foundUser = LorcanaData.database.tournamentTracker.selectForUser(
        discordGuild = guildOrAuthorFallback.value.toLong(),
        discordChannel = channel.id.value.toLong(),
        discordUser = user
    ).find { it.tournament.id == trackedTournament.id }

    if (null == foundUser) {
        respondPublic {
            field("Error") { onNotEnrolledError(user) }
        }

        return
    }

    val tournament = Tournament(trackedTournament.tournament)
    val roundId = tournament.matches().mapIndexed { index, match -> index to match }
        .find { roundIndex - 1 == it.first }?.second?.id!!

    LorcanaData.database.tournamentTracker.insert(
        trackedTournament = trackedTournament,
        tournamentUser = foundUser,
        roundId = roundId,
        againstColor1 = color1.toString(),
        againstColor2 = color2.toString()
    )

    respondPublic {
        field("Tracking", inline = false) {
            onSuccess(foundUser, color1, color2)
        }
    }
}
