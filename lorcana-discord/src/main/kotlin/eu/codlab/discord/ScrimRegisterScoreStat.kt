package eu.codlab.discord

import eu.codlab.discord.chart.Axis
import eu.codlab.discord.chart.Chart
import eu.codlab.discord.chart.Data
import eu.codlab.discord.chart.DataSet
import eu.codlab.discord.chart.Elements
import eu.codlab.discord.chart.Options
import eu.codlab.discord.chart.Scales
import eu.codlab.discord.database.models.ScrimDeck
import eu.codlab.discord.utils.BotPermissions
import eu.codlab.discord.utils.LorcanaData
import me.jakejmattson.discordkt.commands.commands

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

                fun dataSet(
                    borderColor: String,
                    backgroundColor: String,
                    extract: (ScrimDeckResult) -> Int
                ): DataSet {
                    return DataSet(
                        borderColor = borderColor,
                        backgroundColor = backgroundColor,
                        label = "",
                        data = data.map { extract(it) },
                        fill = 1
                    )
                }

                val chart = Chart(
                    type = "line",
                    data = Data(
                        labels = data.map {
                            "${inkPairs[it.deck.color1].first} ${inkPairs[it.deck.color2].first}"
                        },
                        datasets = listOf(
                            dataSet("#038f0f", "#038f0f66") { it.rounds2 },
                            dataSet("#8f5903", "#8f590366") { it.rounds1 },
                            dataSet("#750404", "#75040466") { it.rounds0 },
                        )
                    ),
                    options = Options(
                        spanGaps = false,
                        elements = Elements(),
                        scales = Scales(
                            xAxes = listOf(Axis()),
                            yAxes = listOf(Axis(stacked = true))
                        )
                    )
                )

                println(chart.toJson(serializerD = Data.serializer(DataSet.serializer())))

                respondPublic {
                    image = chart.toUrl(serializerD = Data.serializer(DataSet.serializer()))
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

private data class ScrimDeckResult(
    val deck: ScrimDeck,
    var rounds2: Int = 0,
    var rounds1: Int = 0,
    var rounds0: Int = 0
)
