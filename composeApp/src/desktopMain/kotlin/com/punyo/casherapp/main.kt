package com.punyo.casherapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CasherApplication",
    ) {
        App()
    }
}