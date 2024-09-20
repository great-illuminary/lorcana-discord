package eu.codlab.discord

import eu.codlab.discord.transform.InkArg
import eu.codlab.discord.transform.toInkColor
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import eu.codlab.lorcana.cards.InkColor
import eu.codlab.melee.Tournament
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands

fun tournamentValidateRound() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("validate", "Track yourself in a tournament") {
        execute(
            IntegerArg("round_index", "The round you register the info for, (eg. 1)"),
            InkArg("color_1", "The main color of your opponent's deck"),
            InkArg("color_2", "The 2nd color of your opponent's deck"),
        ) {
            val roundIndex = args.first
            val color1 = args.second.toInkColor() ?: InkColor.Amber
            val color2 = args.third.toInkColor() ?: InkColor.Amber

            val trackedTournament = currentTournament

            if (null == trackedTournament) {
                respondPublic {
                    field("Error") { "No tournament currently tracked" }
                }

                return@execute
            }

            val foundUser = LorcanaData.database.tournamentTracker.selectForUser(
                discordGuild = guildOrAuthorFallback.value.toLong(),
                discordChannel = channel.id.value.toLong(),
                discordUser = author.id.value.toLong()
            ).find { it.tournament.id == trackedTournament.id }

            if (null == foundUser) {
                respondPublic {
                    field("Error") { "You are not enrolled in this tournament" }
                }

                return@execute
            }

            val tournament = Tournament(trackedTournament.tournament)
            val roundId = tournament.matches().mapIndexed { index, match -> index to match }
                .find { roundIndex - 1 == it.first }?.second?.id!!

            val inserted = LorcanaData.database.tournamentTracker.insert(
                trackedTournament = trackedTournament,
                tournamentUser = foundUser,
                roundId = roundId,
                againstColor1 = color1.toString(),
                againstColor2 = color2.toString()
            )

            respondPublic {
                field("Tracking", inline = false) {
                    "You set your opponent's colors to $color1 / $color2"
                }
            }
        }
    }
}
