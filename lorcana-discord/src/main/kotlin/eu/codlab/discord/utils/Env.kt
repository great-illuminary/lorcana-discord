package eu.codlab.discord.utils

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Env(
    @SerialName("bot_token")
    val botToken: String
)
