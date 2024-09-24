package eu.codlab.discord.database

import eu.codlab.discord.database.local.report.ScrimDeckQueries
import eu.codlab.discord.database.local.report.ScrimReportQueries
import eu.codlab.discord.database.models.ScrimDeck
import eu.codlab.discord.database.models.ScrimPlayerResult
import eu.codlab.discord.database.models.ScrimResult
import korlibs.time.DateTime

class ScrimController
internal constructor(
    private val scrimDeckQueries: ScrimDeckQueries,
    private val scrimReportQueries: ScrimReportQueries
) : AbstractQueueController() {
    private fun now() = DateTime.now().unixMillisLong / 1000

    suspend fun selectDecks() = post { scrimDeckQueries.select().executeAsList() }

    suspend fun selectReports(discord: Long) = post {
        scrimReportQueries.select(discord).executeAsList()
            .map {
                ScrimResult(
                    player1 = ScrimPlayerResult(
                        discordId = it.discord_user_id1,
                        roundWon = it.deck1_won_rounds.toInt()
                    ),
                    player2 = ScrimPlayerResult(
                        discordId = it.discord_user_id2,
                        roundWon = it.deck2_won_rounds.toInt()
                    ),
                    deck1 = ScrimDeck(
                        color1 = it.deck1_color1.toInt(),
                        color2 = it.deck1_color2.toInt()
                    ),
                    deck2 = ScrimDeck(
                        color1 = it.deck2_color1.toInt(),
                        color2 = it.deck2_color2.toInt()
                    )
                )
            }
    }

    suspend fun insert(
        discordGuildId: Long,
        timestamp: Long,
        player1: ScrimPlayerResult,
        player2: ScrimPlayerResult
    ) = post {
        val deck1 = scrimDeckQueries.selectForDiscordUserId(player1.discordId).executeAsOneOrNull()
            ?: throw IllegalStateException("No deck set for <@${player1.discordId}>")
        val deck2 = scrimDeckQueries.selectForDiscordUserId(player2.discordId).executeAsOneOrNull()
            ?: throw IllegalStateException("No deck set for <@${player2.discordId}>")

        scrimReportQueries.insert(
            discordGuildId, timestamp,
            deck1 = deck1.id,
            deck2 = deck2.id,
            deck1_won_rounds = player1.roundWon.toLong(),
            deck2_won_rounds = player2.roundWon.toLong()
        )
    }

    suspend fun insertOrUpdate(
        discordUser: Long,
        color1: Int,
        color2: Int,
        lastSelectionAt: Long
    ) = post {
        val color1L = color1.toLong()
        val color2L = color2.toLong()

        val deck = scrimDeckQueries.selectForDiscordUserIdAndColor(discordUser, color1L, color2L)
            .executeAsList().firstOrNull()

        if (null != deck) {
            scrimDeckQueries.set_selection_at(lastSelectionAt, discordUser, color1L, color2L)
            deck.copy(last_selection_at = lastSelectionAt)
        } else {
            scrimDeckQueries.insert(discordUser, color1L, color2L, lastSelectionAt)

            scrimDeckQueries.selectForDiscordUserIdAndColor(discordUser, color1L, color2L)
                .executeAsList().first()
        }
    }
}
