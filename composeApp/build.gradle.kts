import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm("desktop")

    sourceSets {
        @Suppress("unused")
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.compose.material3.adaptive)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.core.viewmodel)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.aay.chart)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        @Suppress("unused")
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        @Suppress("unused")
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
                implementation(libs.sqldelight.driver.sqlite)
                implementation(libs.javacv.platform)
                implementation(libs.zxing.core)
                implementation(libs.zxing.javase)
                implementation(libs.webcam.capture)
            }
        }
    }
}

ktlint {
    filter {
        exclude { element -> element.file.path.contains("generated") }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.punyo.casherapp.application.db")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.punyo.casherapp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.punyo.casherapp"
            packageVersion = "1.0.0"
        }
    }
}
