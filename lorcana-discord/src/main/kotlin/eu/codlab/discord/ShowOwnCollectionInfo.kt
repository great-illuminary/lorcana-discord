package eu.codlab.discord

import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.commands.commands

fun showOwnCollectionInfo() = commands("Collection", BotPermissions.EVERYONE) {
    globalSlash("showOwnCollectionInfo", "Show your own collection info") {
        execute {
            val list = LorcanaData.database.localCollection.selectForUser(author.id.value.toLong())

            LorcanaData.database.localCollection.selectForUser(author.id.value.toLong())

            respond {
                field("Number of item registered") {
                    "${list.size} known items"
                }
            }
        }
    }
}
