@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(additionals.plugins.kotlin.jvm)
    alias(additionals.plugins.kotlin.serialization)
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
    implementation(additionals.multiplatform.file.access)
    implementation(additionals.multiplatform.string.fuzzywuzzy)

    testImplementation(kotlin("test"))
    testImplementation(additionals.kotlinx.coroutines.test)
    testImplementation(additionals.multiplatform.platform)

    implementation(project(":lorcana-discord-database"))
    implementation(project(":melee-api"))
}
