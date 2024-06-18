package eu.codlab.discord

import eu.codlab.discord.transform.CardNumberArg
import eu.codlab.discord.transform.SetArg
import eu.codlab.discord.transform.toSetDescription
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import korlibs.datastructure.iterators.parallelMap
import me.jakejmattson.discordkt.commands.commands

fun addToOwnCollection() = commands("Collection", BotPermissions.EVERYONE) {
    globalSlash("addToOwnCollection", "Show your own collection info") {
        execute(
            SetArg("set", "Specify which set to use"),
            CardNumberArg("cards")
        ) {
            val set = args.first.toSetDescription()
            val cards = args.second
            val localCollection = LorcanaData.database.localCollection

            val discordUser = author.id.value.toLong()
            val list = localCollection.selectForUser(discordUser)

            if (null == set) {
                respond("invalid set description")
                return@execute
            }

            cards.forEach {
                localCollection.insertOrUpdate(
                    discordUser = discordUser,
                    cardSet = set.name,
                    cardId = it.id.toLong(),
                    errata = false,
                    number = it.number.toLong()
                )
            }

            val result = localCollection.selectForUser(author.id.value.toLong())

            val total = result.parallelMap { it.number }
                .reduce { acc, value -> acc + value }

            respond {
                field("Number of item registered") {
                    "${list.size} known items"
                }
                field("Cards registered") {
                    "${result.size} different variants"
                }
                field("New collection status") {
                    "$total total number"
                }
            }
        }
    }
}
