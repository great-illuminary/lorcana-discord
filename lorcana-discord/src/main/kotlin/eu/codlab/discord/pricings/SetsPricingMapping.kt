package eu.codlab.discord.pricings

import eu.codlab.lorcana.raw.SetDescription
import eu.codlab.tcg.pricing.presets.Categories

object SetsPricingMapping {
    val maps: Map<SetDescription, List<Categories.Lorcana>> = mapOf(
        SetDescription.TFC to listOf(Categories.Lorcana.TheFirstChapter),
        SetDescription.RotF to listOf(Categories.Lorcana.RiseOfTheFloodBorn),
        SetDescription.ItI to listOf(Categories.Lorcana.IntoTheInklands),
        SetDescription.P1 to listOf(
            Categories.Lorcana.D23,
            Categories.Lorcana.D100,
            Categories.Lorcana.DisneyLorcanaPromoCards
        )
    )
}
