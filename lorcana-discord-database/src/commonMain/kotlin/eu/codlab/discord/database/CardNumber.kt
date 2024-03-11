package eu.codlab.discord.database

data class CardNumber(
    val cardSet: String,
    val cardId: Long,
    val errata: Boolean,
    val number: Long
)
