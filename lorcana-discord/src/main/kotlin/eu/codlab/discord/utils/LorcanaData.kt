package eu.codlab.discord.utils

import eu.codlab.lorcana.Lorcana
import eu.codlab.lorcana.LorcanaLoaded

object LorcanaData {
    lateinit var lorcanaLoaded: LorcanaLoaded
        private set

    suspend fun initialize() {
        lorcanaLoaded = Lorcana().loadFromResources()
    }
}
