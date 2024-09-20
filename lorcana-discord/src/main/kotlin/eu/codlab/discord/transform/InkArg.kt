package eu.codlab.discord.transform

import eu.codlab.lorcana.cards.InkColor
import eu.codlab.lorcana.raw.SetDescription
import me.jakejmattson.discordkt.arguments.AnyArg

private val enumerations = InkColor.entries.associateBy { it.name.lowercase() }

fun String.toInkColor(): InkColor? = enumerations[this.lowercase()]

@Suppress("FunctionNaming")
fun InkArg(name: String, description: String) = AnyArg(name, description).let {
    it.autocomplete { enumerations.keys.toList() }
}
