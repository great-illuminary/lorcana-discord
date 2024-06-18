package eu.codlab.discord.transform

import eu.codlab.lorcana.raw.SetDescription
import me.jakejmattson.discordkt.arguments.AnyArg

private val enumerations = SetDescription.entries.associateBy { it.name.lowercase() }

fun String.toSetDescription(): SetDescription? = enumerations[this.lowercase()]

fun SetArg(name: String, description: String) = AnyArg(name, description).let {
    it.autocomplete { enumerations.keys.toList() }
}