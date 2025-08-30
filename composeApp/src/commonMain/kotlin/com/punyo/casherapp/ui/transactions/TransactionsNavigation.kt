package com.punyo.casherapp.ui.transactions

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavController.navigateToTransactions() {
    navigate(NavigationDestinations.TRANSACTIONS_ROUTE) {
        launchSingleTop = true
    }
}

fun NavController.navigateToAllTransactions() {
    navigate(NavigationDestinations.ALL_TRANSACTIONS_ROUTE)
}

fun NavController.navigateToProductsList() {
    navigate(NavigationDestinations.PRODUCTS_LIST_ROUTE)
}

fun NavGraphBuilder.transactionsScreen(navController: NavController) {
    composable(route = NavigationDestinations.TRANSACTIONS_ROUTE) {
        TransactionsScreen(navController = navController)
    }
}

fun NavGraphBuilder.allTransactionsSubScreen(navController: NavController) {
    composable(route = NavigationDestinations.ALL_TRANSACTIONS_ROUTE) {
        AllTransactionsSubScreen { navController.popBackStack() }
    }
}

fun NavGraphBuilder.productsListSubScreen(navController: NavController) {
    composable(route = NavigationDestinations.PRODUCTS_LIST_ROUTE) {
        ProductsListSubScreen { navController.popBackStack() }
    }
}
