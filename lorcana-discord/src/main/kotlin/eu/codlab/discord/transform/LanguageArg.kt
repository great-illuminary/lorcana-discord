package eu.codlab.discord.transform

import eu.codlab.lorcana.cards.Language
import me.jakejmattson.discordkt.arguments.AnyArg

private val enumerations = Language.entries.associateBy { it.name.lowercase() }

fun String.toLanguage(): Language? = enumerations[this.lowercase()]

fun LanguageArg(name: String, description: String) = AnyArg(name, description).let {
    it.autocomplete { enumerations.keys.toList() }
}