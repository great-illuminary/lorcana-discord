package eu.codlab.discord.chart

import io.ktor.http.encodeURLPath
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val jsonEncoder = Json {
    encodeDefaults = true
}

@Serializable
data class Chart<D>(
    val type: String = "outlabeledPie",
    val data: D,
    val options: Options = Options()
) {
    fun toJson(
        json: Json = jsonEncoder,
        serializerD: KSerializer<D>
    ) = json.encodeToString(serializer(serializerD), this)
        .replace(")\"", ")")
        .replace("\"getGradientFillHelper(", "getGradientFillHelper(")

    fun toUrl(
        json: Json = jsonEncoder,
        serializerD: KSerializer<D>
    ) = "https://quickchart.io/chart?c=" + toJson(json, serializerD).encodeURLPath()
}

@Serializable
data class Data<D>(
    val labels: List<String>,
    val datasets: List<D>
)

@Serializable
data class DataSetArray(
    val backgroundColor: List<String>,
    val data: List<Int>,
)

@Serializable
data class DataSet(
    val backgroundColor: String,
    /**
     * Used in graphs like radars
     */
    val borderColor: String? = null,
    /**
     * Used in multi label graphs. The label would then be specific for the curve
     */
    val label: String? = null,
    val data: List<Int>,
    /**
     * For graphs which accepts to be filled, -1 seems to be filled, 1 is transparent (weird tho)
     */
    val fill: Int? = null
)

@Serializable
data class Options(
    val spanGaps: Boolean? = null,
    val elements: Elements? = null,
    val plugins: Plugins = Plugins(),
    val scales: Scales? = null
)

@Serializable
data class Scales(
    val xAxes: List<Axis>,
    val yAxes: List<Axis>,
)

@Serializable
data class Axis(
    val stacked: Boolean = false
)

@Serializable
data class Elements(
    val line: Line = Line()
)

@Serializable
data class Line(
    val tension: Double = 0.000001
)

@Serializable
data class Plugins(
    val legend: Boolean = false,
    val outlabels: OutLabels = OutLabels()
)

@Serializable
data class OutLabels(
    val text: String = "%l %p",
    val color: String = "white",
    val stretch: Int = 35,
    val font: Font = Font()
)

@Serializable
data class Font(
    val resizable: Boolean = true,
    val minSize: Int = 12,
    val maxSize: Int = 18
)
