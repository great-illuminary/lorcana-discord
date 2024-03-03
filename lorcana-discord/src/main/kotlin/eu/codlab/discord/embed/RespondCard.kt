package eu.codlab.discord.embed

import dev.kord.rest.builder.message.EmbedBuilder
import eu.codlab.discord.utils.LorcanaData
import eu.codlab.lorcana.raw.SetDescription
import eu.codlab.lorcana.raw.VirtualCard

fun EmbedBuilder.cardContent(
    lang: String,
    set: SetDescription,
    id: Int,
    card: VirtualCard
) {
    val placeholders = LorcanaData.lorcanaLoaded.configuration.placeholders

    val imageUrl = LorcanaData.lorcanaLoaded.configuration.image(
        set,
        lang,
        id
    )

    val languages = card.languages[lang]
    val name = listOf(
        languages?.name ?: "<not translated>",
        languages?.title ?: ""
    ).filter { it.isNotEmpty() }.joinToString(" - ")
    val classifications = card.classifications.map {
        when (lang) {
            "fr" -> it.fr
            "de" -> it.de
            else -> it.en
        }
    }.joinToString(", ")

    val stats = listOf(
        "" to (if (card.inkwell) "Inkable" else "Non Inkable"),
        placeholders["ink"] to card.cost,
        placeholders["strength"] to card.attack,
        placeholders["defence"] to card.defence
    ).mapNotNull { (placeholder, value) ->
        if (null != value) "$placeholder $value".trim() else null
    }.joinToString(" / ")

    val sets = card.sets.keys

    title = name
    image = imageUrl
    thumbnail { url = imageUrl }

    field("Classifications", inline = false) { classifications }

    if (stats.isNotEmpty()) {
        field("Stats", inline = false) { stats }
    }

    sets.forEach { setDescription ->
        card.sets[setDescription]!!.forEach { setItem ->
            field("${setDescription.name} #${setItem.id}") {
                "${setItem.rarity} / by ${setItem.illustrator ?: card.illustrator}"
            }
        }
    }
}
