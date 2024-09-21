package eu.codlab.discord

import eu.codlab.discord.database.models.TournamentUserRound
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import eu.codlab.melee.Tournament
import me.jakejmattson.discordkt.commands.commands

@Suppress("TooGenericExceptionCaught")
fun tournamentStatusMatches() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("status", "Get the list of colors played against during the various rounds") {
        execute {
            val found = currentTournament

            if (null == found) {
                respondPublic {
                    field("Error") { "No tournament currently tracked" }
                }

                return@execute
            }

            val tournamentTracker = LorcanaData.database.tournamentTracker

            try {
                val savedColors = mutableMapOf<String, MutableList<TournamentUserRound>>()
                val tournament = Tournament(found.tournament)
                val matches = tournament.matches()
                val users = LorcanaData.database.tournamentTracker.selectForTournament(found)

                matches.forEach { savedColors.putIfAbsent(it.id, mutableListOf()) }

                println(matches)

                users.forEach { user ->
                    println("checking user ${user.discordUser} ${user.meleeUsername}")
                    val rounds = tournamentTracker.selectRounds(found, user)

                    println("found rounds ? ${rounds.size}")

                    rounds.forEach { round ->
                        savedColors[round.roundIndex]?.add(round)
                    }
                }

                respondPublic {
                    matches.forEachIndexed { index, match ->
                        field("Round ${index + 1}") {
                            val savedColor = savedColors[match.id]!!

                            if (savedColors.isEmpty()) {
                                "No data for this round"
                            } else {
                                savedColor.joinToString("\n") {
                                    "<@${it.user.discordUser}> -> against " +
                                            it.againstColor1?.inkColorEmoji +
                                            " " + it.againstColor2?.inkColorEmoji
                                }
                            }
                        }
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

val String.inkColorEmoji: String
    get() {
        val color = when (lowercase()) {
            "amber" -> "yellow"
            "amethyst" -> "purple"
            "emerald" -> "green"
            "ruby" -> "red"
            "sapphire" -> "blue"
            "steel" -> "white"
            else -> "yellow"
        }

        return ":${color}_circle:"
    }
