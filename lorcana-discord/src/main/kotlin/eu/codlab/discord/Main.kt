package eu.codlab.discord

import dev.kord.x.emoji.Emojis
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.Env
import eu.codlab.discord.utils.LorcanaData.lorcanaLoaded
import eu.codlab.files.VirtualFile
import eu.codlab.lorcana.Lorcana
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import me.jakejmattson.discordkt.dsl.CommandException
import me.jakejmattson.discordkt.dsl.ListenerException
import me.jakejmattson.discordkt.dsl.bot
import net.mamoe.yamlkt.Yaml
import java.awt.Color
import java.io.FileNotFoundException

fun main() {
    val error = ".env.yml doesn't exist. You need to create it and add a bot_token value"
    val currentDir = VirtualFile(VirtualFile.Root, ".env.yml")
    val yaml = Yaml { /** nothing */ }

    runBlocking {
        if (!currentDir.exists()) {
            throw FileNotFoundException(error)
        }

        lorcanaLoaded = Lorcana().loadFromResources()

        val context = currentDir.readString()
        val env: Env = yaml.decodeFromString(context)

        println(env)

        println("commands:")

        bot(env.botToken) {
            configure {
                // Allow a mention to be used in front of commands ('@Bot help').
                mentionAsPrefix = true

                // Whether to show registered entity information on startup.
                logStartup = true

                // Whether to generate documentation for registered commands.
                documentCommands = true

                // Whether to recommend commands when an invalid one is invoked.
                recommendCommands = true

                // Allow users to search for a command by typing 'search <command name>'.
                searchCommands = true

                // Remove a command invocation message after the command is executed.
                deleteInvocation = true

                // Allow slash commands to be invoked as text commands.
                dualRegistry = true

                // An emoji added when a command is invoked (use 'null' to disable this).
                commandReaction = Emojis.eyes

                // A color constant for your bot - typically used in embeds.
                @Suppress("MagicNumber")
                theme = Color(0x00BFFF)

                // Configure the Discord Gateway intents for your bot.
                // intents = Intents.NON_PRIVILEGED + intentsOf<MessageCreateEvent>()

                // Set the default permission required for slash commands.
                defaultPermissions = BotPermissions.EVERYONE
            }

            prefix {
                "/"
            }

            onException {
                if (exception is IllegalArgumentException) {
                    return@onException
                }

                val exceptionName = exception::class.simpleName

                when (this) {
                    is CommandException -> println(
                        "Exception '$exceptionName' in command ${event.command?.name}"
                    )

                    is ListenerException -> println(
                        "Exception '$exceptionName' in listener ${event::class.simpleName}"
                    )
                }
            }

            presence {
                playing("Playing Lorcana")
            }

            onStart {
                val guilds = kord.guilds.toList()
                println("Guilds: ${guilds.joinToString { it.name }}")
            }
        }
    }
}
