package eu.codlab.discord.transform

import eu.codlab.lorcana.cards.Language
import me.jakejmattson.discordkt.arguments.Error
import me.jakejmattson.discordkt.arguments.Result
import me.jakejmattson.discordkt.arguments.StringArgument
import me.jakejmattson.discordkt.arguments.Success
import me.jakejmattson.discordkt.commands.DiscordContext

private class LanguageArg(
    override val name: String,
    override val description: String
) : StringArgument<Language> {
    private val enumerations = Language.entries.associateBy { it.name.lowercase() }

    /**
     * The available choices. Can be any type, but associated by toString value.
     */
    val choices: List<Language> = Language.entries.toList()

    val keys: List<String>
        get() = enumerations.keys.toList()

    init {
        autocomplete { enumerations.keys.toList() }
    }

    override suspend fun transform(input: String, context: DiscordContext): Result<Language> {
        val selection = enumerations[input.lowercase()]
            ?: return Error("Invalid selection")

        return Success(selection)
    }

    override suspend fun generateExamples(context: DiscordContext): List<String> =
        choices.map { it.toString() }
}

fun langArg(name: String, description: String = "Select a language") =
    LanguageArg(name, description).let { arg ->
        arg.autocomplete { arg.keys }
    }
