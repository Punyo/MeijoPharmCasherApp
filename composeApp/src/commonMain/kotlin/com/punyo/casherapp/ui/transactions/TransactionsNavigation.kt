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

fun NavGraphBuilder.transactionsScreen() {
    composable(route = NavigationDestinations.TRANSACTIONS_ROUTE) {
        TransactionsScreen()
    }
}
