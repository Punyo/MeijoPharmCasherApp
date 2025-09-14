package com.punyo.casherapp.ui.register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavController.navigateToRegister() {
    navigate(NavigationDestinations.REGISTER_ROUTE) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.registerScreen() {
    composable(route = NavigationDestinations.REGISTER_ROUTE) {
        RegisterScreen()
    }
}
