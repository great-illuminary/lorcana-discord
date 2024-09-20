plugins {
    alias(additionals.plugins.kotlin.multiplatform)
    alias(additionals.plugins.kotlin.serialization)
}

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
                api(additionals.kotlinx.coroutines)
                api(additionals.kotlinx.coroutines.core)
                api(additionals.kotlinx.serialization.json)

                api(libs.ksoup.lite)
                api(additionals.multiplatform.http.client)
            }
        }
        val commonTest by getting {
        }

        val jvmMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(additionals.kotlinx.coroutines.jvm)
            }
        }
    }
}
