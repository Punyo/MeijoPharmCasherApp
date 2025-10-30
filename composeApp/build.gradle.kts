import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.aboutlibraries)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
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
                implementation(libs.androidx.paging.common)
                implementation(libs.androidx.paging.compose)
                implementation(libs.joda.money)
                implementation(libs.aboutlibraries.core)
                implementation(libs.aboutlibraries.compose.m3)
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
        application {
            buildTypes.release.proguard {
                isEnabled.set(false)
            }
        }
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MeijoPharmCasherApp"
            packageVersion = project.findProperty("app.version") as String? ?: "1.0.0"
            description = "名城大学薬学部大学祭実行委員会内部向けに作成されたアプリケーション"
            modules("java.sql")
            windows {
                menuGroup = "MeijoPharmCasherApp"
                upgradeUuid = localProperties.getProperty("windows.upgradeUuid")
                menu = true
                dirChooser = true
                shortcut = true
            }
        }
    }
}
