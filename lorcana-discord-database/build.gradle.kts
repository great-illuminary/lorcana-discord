@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.sqldelight)
    alias(additionals.plugins.kotlin.multiplatform)
    alias(additionals.plugins.kotlin.serialization)
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
            kotlinOptions.jvmTarget = additionals.versions.java.get()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.sqldelight.runtime)
                api(additionals.kotlinx.coroutines)
                api(additionals.kotlinx.coroutines.core)
                api(additionals.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
        }

        val jvmMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.sqldelight.driver.sqlite)
                implementation(additionals.kotlinx.coroutines.jvm)
                implementation(additionals.multiplatform.file.access)
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateSqlDelightInterface")
    kotlinOptions {
        jvmTarget = additionals.versions.java.get()
    }
}