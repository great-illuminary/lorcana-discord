package eu.codlab.discord.database

import eu.codlab.discord.database.internals.createDriver
import eu.codlab.discord.database.local.LocalDatabase

data class Database internal constructor(
    private val database: LocalDatabase,
    val localCollection: LocalCollection
) {
    companion object {
        fun create(): Database {
            val driver = createDriver(LocalDatabase.Schema, "database.db")

            val database = LocalDatabase(driver)

            return Database(
                database,
                LocalCollection(database.localCollectionQueries)
            )
        }
    }
}
