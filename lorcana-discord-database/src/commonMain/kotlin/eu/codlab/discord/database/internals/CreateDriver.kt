package eu.codlab.discord.database.internals

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

internal expect fun createDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    dbFile: String
): SqlDriver
