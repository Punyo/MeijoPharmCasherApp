package com.punyo.casherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.punyo.casherapp.ui.about.aboutScreen
import com.punyo.casherapp.ui.home.homeScreen
import com.punyo.casherapp.ui.product.productScreen
import com.punyo.casherapp.ui.settings.settingsScreen
import com.punyo.casherapp.ui.transactions.allTransactionsScreen
import com.punyo.casherapp.ui.transactions.transactionsScreen

@Composable
fun CasherAppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestinations.HOME_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        homeScreen()
        productScreen()
        transactionsScreen(navController)
        allTransactionsScreen(navController)
        settingsScreen()
        aboutScreen()
    }
}
