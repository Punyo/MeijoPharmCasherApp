package com.punyo.casherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.punyo.casherapp.ui.about.aboutScreen
import com.punyo.casherapp.ui.home.homeScreen
import com.punyo.casherapp.ui.product.productScreen
import com.punyo.casherapp.ui.settings.settingsScreen
// import com.punyo.casherapp.ui.transactions.allTransactionsSubScreen
// import com.punyo.casherapp.ui.transactions.productsListSubScreen
import com.punyo.casherapp.ui.transactions.transactionsScreen
import com.punyo.casherapp.ui.transactions.transactionsSubScreen

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
        transactionsSubScreen { navController.popBackStack() }
        settingsScreen()
        aboutScreen()
    }
}
