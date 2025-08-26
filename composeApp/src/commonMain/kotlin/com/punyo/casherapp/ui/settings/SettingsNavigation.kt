package com.punyo.casherapp.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavController.navigateToSettings() {
    navigate(NavigationDestinations.SETTINGS_ROUTE) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.settingsScreen() {
    composable(route = NavigationDestinations.SETTINGS_ROUTE) {
        SettingsScreen()
    }
}
