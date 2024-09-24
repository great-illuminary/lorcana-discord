package eu.codlab.discord

import eu.codlab.discord.transform.InkArg
import eu.codlab.discord.transform.toInkColor
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import korlibs.time.DateTime
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.commands.commands

@Suppress("TooGenericExceptionCaught")
fun scrimRegisterDeck() = commands("Scrim", BotPermissions.EVERYONE) {
    globalSlash("deck", "Register your deck colors") {
        execute(
            InkArg("color1", "The first color"),
            InkArg("color2", "The second color")
        ) {
            val scrimTrack = LorcanaData.database.scrimTracker

            val color1 = args.first.toInkColor()!!
            val color2 = args.second.toInkColor()!!

            try {
                scrimTrack.insertOrUpdate(
                    author.id.value.toLong(),
                    color1 = color1.ordinal,
                    color2 = color2.ordinal,
                    lastSelectionAt = DateTime.now().unixMillisLong / 1000
                )

                respondPublic {
                    field("Deck") {
                        "Your deck has been set. It will be automatically used until you change it"
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
