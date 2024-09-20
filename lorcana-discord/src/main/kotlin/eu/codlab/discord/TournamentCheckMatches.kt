package eu.codlab.discord

import eu.codlab.discord.database.models.TournamentUser
import eu.codlab.discord.embed.tournamentMatchesContent
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import eu.codlab.melee.Match
import eu.codlab.melee.Tournament
import me.jakejmattson.discordkt.commands.commands

fun tournamentCheckMatches() = commands("Tournament", BotPermissions.EVERYONE) {
    globalSlash("check", "Check the round information") {
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
                val invalidUsers = mutableMapOf<String, MutableList<TournamentUser>>()
                val tournament = Tournament(found.tournament)
                val matches = tournament.matches()
                val users = LorcanaData.database.tournamentTracker.selectForTournament(found)

                matches.forEach { invalidUsers.putIfAbsent(it.id, mutableListOf()) }

                println(matches)

                users.forEach { user ->
                    println("checking user ${user.discordUser} ${user.meleeUsername}")
                    val rounds = tournamentTracker.selectRounds(found, user)

                    println("found rounds ? ${rounds.size}")

                    fun Match.isMissing(): Boolean {
                        val expectedRound = rounds.find { it.roundIndex == this.id }
                            ?: return true

                        return expectedRound.let {
                            it.againstColor1.isNullOrBlank() || it.againstColor2.isNullOrBlank()
                        }
                    }

                    val invalids = matches.filter { it.started && it.completed && it.isMissing() }
                    invalids.forEach { invalid ->
                        invalidUsers[invalid.id]!!.add(user)
                    }
                }

                respondPublic {
                    matches.forEachIndexed { index, match ->
                        field("Round ${index + 1}") {
                            val invalids = invalidUsers[match.id]!!

                            if (invalids.isEmpty()) {
                                "Everyone completed their info"
                            } else {
                                "Missing info for : " + invalids.map { "<@${it.discordUser}>" }
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
