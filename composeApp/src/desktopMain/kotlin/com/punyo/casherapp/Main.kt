package com.punyo.casherapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.punyo.casherapp.application.di.appModule
import com.punyo.casherapp.application.di.settingsModule
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(appModule, settingsModule)
    }
    System.setProperty("compose.interop.blending", "true")
    Window(
        onCloseRequest = ::exitApplication,
        title = "CasherApplication",
    ) {
        App()
    }
}
