package com.punyo.casherapp.ui.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavController.navigateToAbout() {
    navigate(NavigationDestinations.ABOUT_ROUTE) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.aboutScreen() {
    composable(route = NavigationDestinations.ABOUT_ROUTE) {
        AboutScreen()
    }
}
