package com.punyo.casherapp.ui.settings.licenses

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavController.navigateToLicenses() {
    navigate(NavigationDestinations.LICENSES_ROUTE) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.licensesScreen(onNavigateBack: () -> Unit) {
    composable(route = NavigationDestinations.LICENSES_ROUTE) {
        LicensesScreen(onNavigateBack = onNavigateBack)
    }
}
