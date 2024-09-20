package eu.codlab.discord

import eu.codlab.discord.embed.tournamentMatchesContent
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.melee.Tournament
import me.jakejmattson.discordkt.commands.commands

@Suppress("TooGenericExceptionCaught")
fun tournamentMatches() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("matches", "Show match information from a specific tournament") {
        execute {
            val found = currentTournament

            if (null == found) {
                respondPublic {
                    field("Error") { "No tournament currently tracked" }
                }

                return@execute
            }

            try {
                val tournament = Tournament(found.tournament)

                val matches = tournament.matches()

                respondPublic {
                    tournamentMatchesContent(matches)
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
