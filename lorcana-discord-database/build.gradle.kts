@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.sqldelight)
    alias(dolbyio.plugins.kotlin.multiplatform)
    alias(dolbyio.plugins.kotlin.serialization)
}

sqldelight {
    databases {
        create("LocalDatabase") {
            packageName.set("eu.codlab.discord.database.local")
        }
    }
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = dolbyio.versions.java.get()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.sqldelight.runtime)
                api(dolbyio.kotlinx.coroutines)
                api(dolbyio.kotlinx.coroutines.core)
                api(dolbyio.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
        }

        val jvmMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.sqldelight.driver.sqlite)
                implementation(dolbyio.kotlinx.coroutines.jvm)
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateSqlDelightInterface")
    kotlinOptions {
        jvmTarget = dolbyio.versions.java.get()
    }
}