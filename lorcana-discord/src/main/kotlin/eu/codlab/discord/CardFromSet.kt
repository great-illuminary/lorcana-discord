package eu.codlab.discord

import eu.codlab.discord.embed.cardContent
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import eu.codlab.discord.utils.SetHelper
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands

fun cardFromSet() = commands("Card", BotPermissions.EVERYONE) {
    slash("show", "Show information about a specific card") {
        execute(
            IntegerArg("set"),
            IntegerArg("id"),
            ChoiceArg(
                "lang",
                "The various language available",
                "en",
                "fr",
                "de",
                "it"
            )
        ) {
            val set = SetHelper.set(this.args.first)
            val id = this.args.second
            val lang = this.args.third

            val card = LorcanaData.lorcanaLoaded.cards.find {
                it.sets.containsKey(set) && null != it.sets[set]?.find { it.id == id }
            }

            if (null != card) {
                respondPublic {
                    cardContent(
                        lang,
                        set,
                        id,
                        card
                    )
                }
            } else {
                respond("card invalid for $set/$id")
            }
        }
    }
}
