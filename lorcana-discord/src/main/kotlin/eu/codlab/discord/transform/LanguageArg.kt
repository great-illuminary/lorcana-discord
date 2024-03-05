package eu.codlab.discord.transform

import eu.codlab.lorcana.cards.Language
import me.jakejmattson.discordkt.arguments.Error
import me.jakejmattson.discordkt.arguments.Result
import me.jakejmattson.discordkt.arguments.StringArgument
import me.jakejmattson.discordkt.arguments.Success
import me.jakejmattson.discordkt.commands.DiscordContext

open class LanguageArg(
    override val name: String,
    override val description: String = "Select a language"
) : StringArgument<Language> {
    private val enumerations = Language.entries.associateBy { it.name.lowercase() }

    /**
     * The available choices. Can be any type, but associated by toString value.
     */
    val choices: List<Language> = Language.entries.toList()

    override suspend fun transform(input: String, context: DiscordContext): Result<Language> {
        val selection = enumerations[input.lowercase()]
            ?: return Error("Invalid selection")

        return Success(selection)
    }

    override suspend fun generateExamples(context: DiscordContext): List<String> =
        choices.map { it.toString() }
}
