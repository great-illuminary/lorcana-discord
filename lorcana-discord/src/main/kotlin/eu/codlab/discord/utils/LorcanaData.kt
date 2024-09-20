package eu.codlab.discord.utils

import eu.codlab.discord.database.Database
import eu.codlab.lorcana.Lorcana
import eu.codlab.lorcana.LorcanaLoaded

object LorcanaData {
    lateinit var database: Database
        private set

    lateinit var lorcanaLoaded: LorcanaLoaded
        private set

    suspend fun initialize() {
        database = Database.create()
        lorcanaLoaded = Lorcana().loadFromResources()
    }
}
