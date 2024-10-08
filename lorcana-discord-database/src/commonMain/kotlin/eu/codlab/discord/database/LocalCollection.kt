package eu.codlab.discord.database

import eu.codlab.discord.database.local.LocalCollectionQueries

class LocalCollection
internal constructor(
    private val queries: LocalCollectionQueries
) : AbstractQueueController() {
    suspend fun selectForUser(discordUser: Long) = post {
        queries.selectForDiscordUser(discordUser)
            .executeAsList()
            .map {
                CardNumber(
                    cardSet = it.card_set,
                    cardId = it.card_id,
                    errata = it.errata > 0,
                    number = it.number
                )
            }
    }

    suspend fun selectAll() = post {
        queries.select()
            .executeAsList()
            .map {
                CardNumber(
                    cardSet = it.card_set,
                    cardId = it.card_id,
                    errata = it.errata > 0,
                    number = it.number
                )
            }
    }

    suspend fun insertOrUpdate(
        discordUser: Long,
        cardSet: String,
        cardId: Long,
        errata: Boolean,
        number: Long
    ) = post {
        println("attempt to add $discordUser/$cardSet/$cardId/$errata/$number")

        val count = queries.countForDiscordUserAndCard(
            discord_user_id = discordUser,
            card_set = cardSet,
            card_id = cardId,
            errata = if (errata) 1 else 0
        ).executeAsOneOrNull() ?: 0

        if (count > 0) {
            println("updating and having $count")
            queries.update(
                discord_user_id = discordUser,
                card_set = cardSet,
                card_id = cardId,
                errata = if (errata) 1 else 0,
                number = number
            )
        } else {
            println("updating and having $count")
            queries.insert(
                discord_user_id = discordUser,
                card_set = cardSet,
                card_id = cardId,
                errata = if (errata) 1 else 0,
                number = number
            )
        }

        queries.selectForDiscordUserAndCard(
            discord_user_id = discordUser,
            card_set = cardSet,
            card_id = cardId,
            errata = if (errata) 1 else 0
        ).executeAsOneOrNull()?.let {
            CardNumber(
                cardSet = it.card_set,
                errata = it.errata > 0,
                cardId = it.card_id,
                number = it.number
            )
        }!!
    }
}
