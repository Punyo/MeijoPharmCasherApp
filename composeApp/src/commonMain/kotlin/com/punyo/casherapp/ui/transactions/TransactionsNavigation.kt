package com.punyo.casherapp.ui.transactions

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.punyo.casherapp.ui.navigation.NavigationDestinations
import com.punyo.casherapp.ui.transactions.alltransactions.AllTransactionsSubScreen
import com.punyo.casherapp.ui.transactions.productlist.ProductsListSubScreen

fun NavController.navigateToTransactions() {
    navigate(NavigationDestinations.TRANSACTIONS_ROUTE) {
        launchSingleTop = true
    }
}

fun NavController.navigateToAllTransactions(timePeriod: TimePeriod) {
    val route =
        TransactionsSubScreenRoute(NavigationDestinations.ALL_TRANSACTIONS_ROUTE, timePeriod.name)
    navigate(route)
}

fun NavController.navigateToProductsList(timePeriod: TimePeriod) {
    val route =
        TransactionsSubScreenRoute(NavigationDestinations.PRODUCTS_LIST_ROUTE, timePeriod.name)
    navigate(route)
}

fun NavGraphBuilder.transactionsScreen(navController: NavController) {
    composable(route = NavigationDestinations.TRANSACTIONS_ROUTE) {
        TransactionsScreen(navController = navController)
    }
}

fun NavGraphBuilder.transactionsSubScreen(
    onBackClick: () -> Unit,
) {
    composable<TransactionsSubScreenRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<TransactionsSubScreenRoute>()
        when (route.route) {
            NavigationDestinations.ALL_TRANSACTIONS_ROUTE -> {
                AllTransactionsSubScreen(timePeriod = route.timePeriod, onNavigateBack = onBackClick)
            }

            NavigationDestinations.PRODUCTS_LIST_ROUTE -> {
                ProductsListSubScreen(timePeriod = route.timePeriod, onNavigateBack = onBackClick)
            }
        }
    }
}
