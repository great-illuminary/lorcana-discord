package eu.codlab.discord

import eu.codlab.discord.chart.Chart
import eu.codlab.discord.chart.Data
import eu.codlab.discord.chart.DataSet
import eu.codlab.discord.database.models.ScrimDeck
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.commands.commands

@Suppress("TooGenericExceptionCaught", "MagicNumber")
fun scrimRegisterStat() = commands("Scrim", BotPermissions.EVERYONE) {
    globalSlash("scrimDeckRatio", "The latest scrim dek ratio") {
        execute {
            val scrimTrack = LorcanaData.database.scrimTracker

            try {
                val reports = scrimTrack.selectReports(guildOrAuthorFallback.value.toLong())
                val decks = mutableMapOf<Long, ScrimDeck>()
                reports.forEach {
                    decks[it.player1.discordId] = it.deck1
                    decks[it.player2.discordId] = it.deck2
                }

                val ratios = mutableMapOf<String, DeckRatio>()
                decks.values.forEach {
                    val key = "${it.color1}_${it.color2}"
                    ratios.putIfAbsent(key, DeckRatio(inkPairs[it.color1], inkPairs[it.color2]))
                    ratios[key]!!.count++
                }

                val data = ratios.values.map { it }.sortedBy { it.count }
                val chart = Chart(
                    data = Data(
                        labels = data.map { it.inkPair1.first + " " + it.inkPair2.first },
                        datasets = listOf(
                            DataSet(
                                backgroundColor = data.map {
                                    "getGradientFillHelper('vertical',['${it.inkPair1.second}', '${it.inkPair2.second}'])"
                                },
                                data = data.map { it.count }
                            )
                        )
                    )
                )

                println(chart.toJson())

                respondPublic {
                    field("Scrim") {
                        "The result has been set"
                    }

                    image = chart.toUrl()

                    // thumbnail { url = chart.toUrl() }
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

private val inkPairs = listOf(
    "A" to "#f5dd42",
    "Am" to "#6b0148",
    "E" to "#128f09",
    "R" to "#850404",
    "Sa" to "#04068c",
    "St" to "#bbbfbf"
)

private data class DeckRatio(
    val inkPair1: Pair<String, String>,
    val inkPair2: Pair<String, String>,
    var count: Int = 0
)
