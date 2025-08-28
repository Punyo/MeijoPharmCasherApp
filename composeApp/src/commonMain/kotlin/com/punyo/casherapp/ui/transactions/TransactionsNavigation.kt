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

fun NavGraphBuilder.transactionsScreen(navController: NavController) {
    composable(route = NavigationDestinations.TRANSACTIONS_ROUTE) {
        TransactionsScreen(navController = navController)
    }
}

fun NavGraphBuilder.allTransactionsScreen(navController: NavController) {
    composable(route = NavigationDestinations.ALL_TRANSACTIONS_ROUTE) {
        AllTransactionsScreen { navController.popBackStack() }
    }
}
