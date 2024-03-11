package eu.codlab.discord.transform

import me.jakejmattson.discordkt.arguments.Result
import me.jakejmattson.discordkt.arguments.StringArgument
import me.jakejmattson.discordkt.arguments.Success
import me.jakejmattson.discordkt.commands.DiscordContext

open class CardNumberArg(
    override val name: String,
    override val description: String = ", separated list of id:number or range via id1-idLast:number"
) : StringArgument<List<CardNumber>> {
    override suspend fun transform(
        input: String,
        context: DiscordContext
    ): Result<List<CardNumber>> {
        val list = input.split(",")
            .mapNotNull { pair ->
                pair.split(":").let {
                    if (it.size != 2) return@let null
                    val left = it[0]
                    val number = it[1].toLongOrNull()

                    val ids = left.split("-")
                    val start = ids.getOrNull(0)?.toLongOrNull()
                    val end = ids.getOrNull(1)?.toLongOrNull()

                    if (start == null || number == null) return@let null

                    if (end == null) {
                        return@let listOf(CardNumber(start, number))
                    }

                    return@let (start..end).map { id -> CardNumber(id, number) }
                }
            }.flatten()

        return Success(list)
    }
}

data class CardNumber(
    val id: Long,
    val number: Long
)
