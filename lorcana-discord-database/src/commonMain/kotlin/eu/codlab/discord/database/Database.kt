package eu.codlab.discord.database

import eu.codlab.discord.database.internals.createDriver
import eu.codlab.discord.database.local.LocalDatabase

data class Database internal constructor(
    private val database: LocalDatabase,
    val localCollection: LocalCollection,
    val tournamentTracker: TournamentController,
    val scrimTracker: ScrimController
) {
    companion object {
        fun create(): Database {
            val driver = createDriver(LocalDatabase.Schema, "database.db")

            val database = LocalDatabase(driver)

            return Database(
                database,
                LocalCollection(database.localCollectionQueries),
                TournamentController(
                    database.tournamentQueries,
                    database.tournamentUserQueries,
                    database.tournamentUserRoundQueries
                ),
                ScrimController(
                    database.scrimDeckQueries,
                    database.scrimReportQueries
                )
            )
        }
    }
}
