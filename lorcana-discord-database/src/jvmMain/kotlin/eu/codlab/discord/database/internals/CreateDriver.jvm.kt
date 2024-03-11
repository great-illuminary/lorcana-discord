package eu.codlab.discord.database.internals

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

internal actual fun createDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    dbFile: String
): SqlDriver {
    val driver = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
    schema.create(driver)
    return driver
}
