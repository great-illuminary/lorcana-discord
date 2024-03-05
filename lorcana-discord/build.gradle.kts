@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(dolbyio.plugins.kotlin.jvm)
    alias(dolbyio.plugins.kotlin.serialization)
    id("jvmCompat")
}

group = "eu.codlab.discord"
version = "1.0"

application {
    mainClass.set("eu.codlab.discord.MainKt")
}

dependencies {
    implementation("eu.codlab:lorcana-data:0.5.0")
    implementation("me.jakejmattson:DiscordKt:0.24.0")
    implementation(dolbyio.multiplatform.file.access)
    implementation(dolbyio.multiplatform.string.fuzzywuzzy)
}
