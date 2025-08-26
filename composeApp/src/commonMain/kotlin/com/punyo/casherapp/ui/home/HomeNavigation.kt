package com.punyo.casherapp.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavController.navigateToHome() {
    navigate(NavigationDestinations.HOME_ROUTE) {
        popUpTo(graph.startDestinationId)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.homeScreen() {
    composable(route = NavigationDestinations.HOME_ROUTE) {
        HomeScreen()
    }
}
