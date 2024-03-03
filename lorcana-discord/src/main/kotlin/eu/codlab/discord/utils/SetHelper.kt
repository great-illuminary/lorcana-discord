package eu.codlab.discord.utils

import eu.codlab.lorcana.raw.SetDescription

object SetHelper {
    @Suppress("MagicNumber")
    fun set(id: Int) = when (id) {
        1 -> SetDescription.TFC
        2 -> SetDescription.RotF
        3 -> SetDescription.ItI
        else -> SetDescription.TFC
    }
}
