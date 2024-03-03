package eu.codlab.discord

import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.commands.commands

fun cardInfo() = commands("General", BotPermissions.EVERYONE) {
    slash("cards", "Show information about the data sets") {
        execute {
            respondPublic("having ${LorcanaData.lorcanaLoaded.cards.size}")
        }
    }
}
