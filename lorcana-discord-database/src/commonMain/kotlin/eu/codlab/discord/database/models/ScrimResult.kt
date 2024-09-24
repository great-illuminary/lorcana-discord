package eu.codlab.discord.database.models

data class ScrimResult(
    val player1: ScrimPlayerResult,
    val player2: ScrimPlayerResult,
    val deck1: ScrimDeck,
    val deck2: ScrimDeck
)
