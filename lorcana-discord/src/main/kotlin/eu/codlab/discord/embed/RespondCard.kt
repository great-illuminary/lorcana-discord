package eu.codlab.discord.embed

import dev.kord.rest.builder.message.EmbedBuilder
import eu.codlab.discord.pricings.PricingController
import eu.codlab.discord.utils.LorcanaData
import eu.codlab.lorcana.cards.ClassificationHolder
import eu.codlab.lorcana.cards.Language
import eu.codlab.lorcana.raw.SetDescription
import eu.codlab.lorcana.raw.Variant
import eu.codlab.lorcana.raw.VirtualCard

@Suppress("LongMethod", "ComplexMethod")
suspend fun EmbedBuilder.cardContent(
    lang: Language,
    set: SetDescription,
    setItem: Variant<ClassificationHolder>,
    card: VirtualCard
) {
    val placeholders = LorcanaData.lorcanaLoaded.configuration.placeholders

    val imageUrl = LorcanaData.lorcanaLoaded.configuration.image(
        set,
        lang,
        setItem.id
    )

    val languages = card.languages[lang.name.lowercase()]
    val name = listOf(
        languages?.name ?: "<not translated>",
        languages?.title ?: ""
    ).filter { it.isNotEmpty() }.joinToString(" - ")
    val rawClassifications = setItem.erratas?.get(lang)?.classifications ?: card.classifications

    val classifications = rawClassifications.map {
        when (lang) {
            Language.Fr -> it.fr
            Language.De -> it.de
            // not yet available -> Language.It -> it.it
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

    val sets = card.variants.map { it.set }.toSet()

    title = name
    image = imageUrl
    thumbnail { url = imageUrl }

    val price = PricingController.price(set, setItem.id)
    println("having price $price")

    field("Classifications", inline = false) { classifications }

    if (stats.isNotEmpty()) {
        field("Stats", inline = false) { stats }
    }

    if (null != setItem.erratas) {
        field("Erratas", inline = false) { "This card contains errors" }
    }

    price?.let {
        field("TCGPlayer", inline = false) {
            "low /$${it.lowPrice}\n" +
                "mid / $${it.midPrice}\n " +
                "high / $${it.highPrice}\n" +
                "(price based upon tcgcsv.com regardless of the lang & in USD"
        }
    }

    sets.forEach { setDescription ->
        field(setDescription.name) {
            var text = ""

            text += card.variants(setDescription).map { setItem ->
                val erratas = if (null != setItem.erratas) {
                    " / Contains errors"
                } else {
                    ""
                }

                "#${setItem.id} ${erratas}\n by ${setItem.illustrator ?: card.illustrator}"
            }.joinToString("\n")

            text
        }
    }
}
