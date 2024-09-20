package eu.codlab.discord.embed

import dev.kord.rest.builder.message.EmbedBuilder
import eu.codlab.melee.Match
import eu.codlab.melee.Tournament

suspend fun EmbedBuilder.tournamentMatchesContent(
    tournament: Tournament,
    matches: List<Match>
) {
    if (matches.isEmpty()) {
        field("Stats", inline = false) {
            "There are currently no matches or standings set into this tournament"
        }
    }

    matches.forEachIndexed { index, match ->
        field("Round ${index + 1}") {
            "Started: ${match.started} / Completed= ${match.completed}"
        }
    }
}
