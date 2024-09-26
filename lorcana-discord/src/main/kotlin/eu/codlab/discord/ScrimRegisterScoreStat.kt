package eu.codlab.discord

import dev.kord.x.emoji.Emojis
import eu.codlab.discord.chart.Axis
import eu.codlab.discord.chart.Chart
import eu.codlab.discord.chart.Data
import eu.codlab.discord.chart.DataSet
import eu.codlab.discord.chart.Elements
import eu.codlab.discord.chart.Options
import eu.codlab.discord.chart.Plugins
import eu.codlab.discord.chart.Scales
import eu.codlab.discord.database.models.ScrimDeck
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.commands.commands
import kotlin.math.round

@Suppress("TooGenericExceptionCaught", "MagicNumber", "LongMethod")
fun scrimRegisterScoreStat() = commands("Scrim", BotPermissions.EVERYONE) {
    globalSlash("scrimScoreRatio", "The latest scrim's score ratio") {
        execute {
            val scrimTrack = LorcanaData.database.scrimTracker

            try {
                val reports = scrimTrack.selectReports(guildOrAuthorFallback.value.toLong())
                val scores = mutableMapOf<String, ScrimDeckResult>()

                reports.forEach { report ->
                    listOf(report.deck1, report.deck2).forEach { deck ->
                        scores.putIfAbsent("${deck.color1}_${deck.color2}", ScrimDeckResult(deck))
                    }

                    listOf(
                        report.player1 to report.deck1,
                        report.player2 to report.deck2
                    ).forEach { (score, deck) ->
                        val id = "${deck.color1}_${deck.color2}"
                        val finalScore = scores[id]!!
                        when (score.roundWon) {
                            2 -> finalScore.rounds2++
                            1 -> finalScore.rounds1++
                            else -> finalScore.rounds0++
                        }
                    }
                }

                val data = scores.values.map { it }
                    .sortedBy { "${it.deck.color1}_${it.deck.color2}" }

                respondMenu {
                    page {
                        title = "Number of games"
                        val chart = createChart(data) { value, _ -> value.toDouble() }
                        image = chart.toUrl(serializerD = Data.serializer(DataSet.serializer()))
                    }

                    page {
                        title = "% of repartition"
                        val chart = createChart(data) { value, total ->
                            value * 100.0 / total
                        }
                        image = chart.toUrl(serializerD = Data.serializer(DataSet.serializer()))
                    }

                    buttons {
                        button("Left", Emojis.arrowLeft) {
                            previousPage()
                        }

                        button("Right", Emojis.arrowRight) {
                            nextPage()
                        }
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

private fun createChart(
    data: List<ScrimDeckResult>,
    computeCount: (Int, Int) -> Double
) = Chart(
    type = "bar",
    data = Data(
        labels = data.map {
            "${inkPairs[it.deck.color1].first} ${inkPairs[it.deck.color2].first}"
        },
        datasets = listOf(
            dataSet("0r", data, "#750404", "#75040466", 2) { deck ->
                computeCount(deck.rounds0, deck.total)
            },
            dataSet("1r", data, "#8f5903", "#8f590366", 0) { deck ->
                computeCount(deck.rounds1, deck.total)
            },
            dataSet("2z", data, "#038f0f", "#038f0f66", 1) { deck ->
                computeCount(deck.rounds2, deck.total)
            },
        )
    ),
    options = Options(
        spanGaps = false,
        elements = Elements(),
        plugins = Plugins(
            legend = true
        ),
        scales = Scales(
            xAxes = listOf(Axis(stacked = true)),
            yAxes = listOf(Axis(stacked = true))
        )
    )
)

private fun dataSet(
    label: String? = null,
    data: List<ScrimDeckResult>,
    borderColor: String,
    backgroundColor: String,
    fill: Int? = null,
    extract: (ScrimDeckResult) -> Double
): DataSet {
    return DataSet(
        borderColor = borderColor,
        backgroundColor = backgroundColor,
        label = label,
        data = data.map { round(extract(it)).toInt() },
        fill = fill
    )
}

private data class ScrimDeckResult(
    val deck: ScrimDeck,
    var rounds2: Int = 0,
    var rounds1: Int = 0,
    var rounds0: Int = 0
) {
    val total: Int
        get() {
            return rounds0 + rounds1 + rounds2
        }
}
