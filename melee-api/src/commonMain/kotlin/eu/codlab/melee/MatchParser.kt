package eu.codlab.melee

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.fleeksoft.ksoup.select.Evaluator
import eu.codlab.http.Configuration
import eu.codlab.http.createClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class MatchParser {
    private val client = createClient(
        Configuration(enableLogs = true)
    ) {
        // nothing
    }

    suspend fun matches(tournamentId: String): List<Match> {
        val url = "https://melee.gg/Tournament/View/$tournamentId"
        val request = client.get(url) {
            header(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Safari/605.1.15"
            )
            header("Location", "melee.gg")
        }

        val body = request.bodyAsText()

        if (!request.status.isSuccess()) {
            throw IllegalStateException("Couldn't load https://melee.gg/Tournament/View/$tournamentId")
        }

        val result = Ksoup.parse(body)

        val elements = result.selectFrom { element ->
            element.id() == "pairings-round-selector-container"
        }

        if (elements.size != 1) {
            throw IllegalStateException("Couldn't find the right number of pairings in $tournamentId")
        }

        val standingCompleted = mutableMapOf<String, Boolean>()

        val standingsParents = result.selectFrom { element ->
            element.id() == "standings-round-selector-container"
        }

        standingsParents.getOrNull(0)?.let { standings ->
            standings.children().forEach { child ->
                val id = child.attribute("data-id")?.value
                val isCompleted = child.attribute("data-is-completed")?.value?.lowercase() == "true"

                id?.let { standingCompleted[it] = isCompleted }
            }
        }

        val matches = elements[0].children().mapNotNull { child ->
            child.attribute("data-id")?.value?.let { id ->
                Match(
                    tournament = tournamentId,
                    id = id,
                    started = child.attribute("data-is-started")?.value?.lowercase() == "true",
                    completed = standingCompleted[id] ?: false
                )
            }
        }

        return matches
    }

    private fun Document.selectFrom(evaluation: (element: Element) -> Boolean): Elements {
        return this.select(
            object : Evaluator() {
                override fun matches(root: Element, element: Element): Boolean {
                    return evaluation(element)
                }
            }
        )
    }
}
