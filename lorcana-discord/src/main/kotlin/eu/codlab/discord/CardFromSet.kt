package eu.codlab.discord

import dev.kord.x.emoji.Emojis
import eu.codlab.discord.embed.cardContent
import eu.codlab.discord.transform.langArg
import eu.codlab.discord.transform.setArg
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands

fun cardFromSet() = commands("Card", BotPermissions.EVERYONE) {
    globalSlash("show", "Show information about a specific card") {
        execute(
            setArg("set"),
            IntegerArg("id"),
            langArg(
                "lang",
                "The various language available"
            )
        ) {
            val set = args.first
            val id = args.second
            val lang = args.third

            val card = LorcanaData.lorcanaLoaded.cards.find { card ->
                null != card.variants.find { it.set == set && it.id == id }
            }

            if (null == card) {
                respond("card invalid for $set/$id")
                return@execute
            }

            val individualCards = card.variants.filter { it.set == set && it.id == id }

            println(individualCards)
            if (individualCards.size > 1) {
                respondMenu {
                    individualCards.forEachIndexed { index, individualCard ->
                        println("showing $individualCard")
                        page {
                            title = "#$index"
                            cardContent(
                                lang,
                                set,
                                individualCard,
                                card
                            )
                        }
                    }

                    buttons {
                        button("Left", Emojis.arrowLeft) {
                            previousPage()
                        }

                        button("Right", Emojis.arrowRight) {
                            nextPage()
                        }
                    }
                }
                return@execute
            }

            respondPublic {
                cardContent(
                    lang,
                    set,
                    individualCards.first(),
                    card
                )
            }
        }
    }
}
