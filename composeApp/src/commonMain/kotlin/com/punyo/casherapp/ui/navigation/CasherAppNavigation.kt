package com.punyo.casherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.punyo.casherapp.ui.product.productScreen
import com.punyo.casherapp.ui.register.registerScreen
import com.punyo.casherapp.ui.settings.settingsScreen
import com.punyo.casherapp.ui.transactions.transactionsScreen
import com.punyo.casherapp.ui.transactions.transactionsSubScreen

@Composable
fun CasherAppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestinations.TRANSACTIONS_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        productScreen()
        transactionsScreen(navController)
        transactionsSubScreen { navController.popBackStack() }
        settingsScreen()
        registerScreen()
    }
}
