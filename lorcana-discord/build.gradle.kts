@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(dolbyio.plugins.kotlin.jvm)
    alias(dolbyio.plugins.kotlin.serialization)
    id("jvmCompat")
}

group = "eu.codlab.discord"
version = property("version")!!

application {
    mainClass.set("eu.codlab.discord.MainKt")
}

dependencies {
    implementation(libs.lorcana.data)
    implementation(libs.tcg.data.pricing)
    implementation(libs.discordkt)
    implementation(dolbyio.multiplatform.file.access)
    implementation(dolbyio.multiplatform.string.fuzzywuzzy)

    testImplementation(kotlin("test"))
    testImplementation(dolbyio.kotlinx.coroutines.test)
    testImplementation(dolbyio.multiplatform.platform)

    implementation(project(":lorcana-discord-database"))
}
