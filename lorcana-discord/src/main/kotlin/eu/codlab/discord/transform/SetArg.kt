package eu.codlab.discord.transform

import eu.codlab.lorcana.raw.SetDescription
import me.jakejmattson.discordkt.arguments.Error
import me.jakejmattson.discordkt.arguments.Result
import me.jakejmattson.discordkt.arguments.StringArgument
import me.jakejmattson.discordkt.arguments.Success
import me.jakejmattson.discordkt.commands.DiscordContext

private class SetArg(
    override val name: String,
    override val description: String
) : StringArgument<SetDescription> {
    private val enumerations = SetDescription.entries.associateBy { it.name.lowercase() }

    val keys: List<String>
        get() = enumerations.keys.toList()

    /**
     * The available choices. Can be any type, but associated by toString value.
     */
    val choices: List<SetDescription> = SetDescription.entries.toList()

    override suspend fun transform(input: String, context: DiscordContext): Result<SetDescription> {
        val selection = enumerations[input.lowercase()]
            ?: return Error("Invalid selection")

        return Success(selection)
    }

    override suspend fun generateExamples(context: DiscordContext): List<String> =
        choices.map { it.toString() }
}

fun setArg(name: String, description: String = "Select a set") =
    SetArg(name, description).let { arg ->
        arg.autocomplete { arg.keys }
    }
