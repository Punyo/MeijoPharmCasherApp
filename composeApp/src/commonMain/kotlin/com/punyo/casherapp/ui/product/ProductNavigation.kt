package com.punyo.casherapp.ui.product

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavController.navigateToProduct() {
    navigate(NavigationDestinations.PRODUCT_ROUTE) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.productScreen() {
    composable(route = NavigationDestinations.PRODUCT_ROUTE) {
        ProductScreen()
    }
}
