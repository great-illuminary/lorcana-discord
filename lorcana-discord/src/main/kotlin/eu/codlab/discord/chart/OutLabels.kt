package eu.codlab.discord.chart

import io.ktor.http.encodeURLPath
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val jsonEncoder = Json {
    encodeDefaults = true
}

@Serializable
data class Chart(
    val type: String = "outlabeledPie",
    val data: Data,
    val options: Options = Options()
) {
    fun toJson(json: Json = jsonEncoder) = json.encodeToString(serializer(), this)
        .replace(")\"", ")")
        .replace("\"getGradientFillHelper(", "getGradientFillHelper(")

    fun toUrl(json: Json = jsonEncoder) =
        "https://quickchart.io/chart?c=" + toJson(json).encodeURLPath()
}

@Serializable
data class Data(
    val labels: List<String>,
    val datasets: List<DataSet>
)

@Serializable
data class DataSet(
    val backgroundColor: List<String>,
    val data: List<Int>
)

@Serializable
data class Options(
    val plugins: Plugins = Plugins()
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
