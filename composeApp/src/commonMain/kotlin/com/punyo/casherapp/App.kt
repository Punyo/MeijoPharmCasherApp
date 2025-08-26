package com.punyo.casherapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.punyo.casherapp.ui.about.navigateToAbout
import com.punyo.casherapp.ui.home.navigateToHome
import com.punyo.casherapp.ui.navigation.CasherAppNavigation
import com.punyo.casherapp.ui.navigation.NavigationDestinations
import com.punyo.casherapp.ui.product.navigateToProduct
import com.punyo.casherapp.ui.settings.navigateToSettings
import com.punyo.casherapp.ui.transactions.navigateToTransactions
import kotlinx.coroutines.launch
import meijopharmcasherapp.composeapp.generated.resources.Res
import meijopharmcasherapp.composeapp.generated.resources.app_name
import meijopharmcasherapp.composeapp.generated.resources.app_subtitle
import meijopharmcasherapp.composeapp.generated.resources.menu
import meijopharmcasherapp.composeapp.generated.resources.nav_about
import meijopharmcasherapp.composeapp.generated.resources.nav_home
import meijopharmcasherapp.composeapp.generated.resources.nav_settings
import meijopharmcasherapp.composeapp.generated.resources.nav_transactions
import meijopharmcasherapp.composeapp.generated.resources.product_title
import org.jetbrains.compose.resources.stringResource

data class DrawerItem(
    val icon: ImageVector,
    val title: String,
    val route: String,
)

@Composable
fun DrawerContent(
    drawerItems: List<DrawerItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    onDrawerClose: () -> Unit = {},
) {
    DrawerHeader()
    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn {
        items(drawerItems) { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedRoute == item.route,
                onClick = {
                    onItemClick(item.route)
                    onDrawerClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsiveNavigationDrawer(
    drawerItems: List<DrawerItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    content: @Composable (showMenuButton: Boolean, onMenuClick: () -> Unit) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val usePermanentDrawer = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    val appContent = remember {
        movableContentOf { showMenuButton: Boolean, onMenuClick: () -> Unit ->
            content(showMenuButton, onMenuClick)
        }
    }

    if (usePermanentDrawer) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(modifier = Modifier.width(240.dp)) {
                    DrawerContent(
                        drawerItems = drawerItems,
                        selectedRoute = selectedRoute,
                        onItemClick = onItemClick,
                    )
                }
            },
        ) {
            appContent(false) {}
        }
    } else {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContent(
                        drawerItems = drawerItems,
                        selectedRoute = selectedRoute,
                        onItemClick = onItemClick,
                        onDrawerClose = {
                            scope.launch {
                                drawerState.close()
                            }
                        },
                    )
                }
            },
        ) {
            appContent(true) {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: NavigationDestinations.HOME_ROUTE

        val drawerItems =
            listOf(
                DrawerItem(Icons.Filled.Home, stringResource(Res.string.nav_home), NavigationDestinations.HOME_ROUTE),
                DrawerItem(Icons.Filled.AccountBalance, stringResource(Res.string.nav_transactions), NavigationDestinations.TRANSACTIONS_ROUTE),
                DrawerItem(Icons.Filled.Settings, stringResource(Res.string.nav_settings), NavigationDestinations.SETTINGS_ROUTE),
                DrawerItem(Icons.Filled.Person, stringResource(Res.string.product_title), NavigationDestinations.PRODUCT_ROUTE),
                DrawerItem(Icons.Filled.Info, stringResource(Res.string.nav_about), NavigationDestinations.ABOUT_ROUTE),
            )

        ResponsiveNavigationDrawer(
            drawerItems = drawerItems,
            selectedRoute = currentRoute,
            onItemClick = { route ->
                when (route) {
                    NavigationDestinations.HOME_ROUTE -> navController.navigateToHome()
                    NavigationDestinations.PRODUCT_ROUTE -> navController.navigateToProduct()
                    NavigationDestinations.TRANSACTIONS_ROUTE -> navController.navigateToTransactions()
                    NavigationDestinations.SETTINGS_ROUTE -> navController.navigateToSettings()
                    NavigationDestinations.ABOUT_ROUTE -> navController.navigateToAbout()
                }
            },
        ) { showMenuButton, onMenuClick ->
            MainContent(
                navController = navController,
                currentRoute = currentRoute,
                drawerItems = drawerItems,
                showMenuButton = showMenuButton,
                onMenuClick = onMenuClick,
            )
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = stringResource(Res.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    navController: NavHostController,
    currentRoute: String,
    drawerItems: List<DrawerItem>,
    showMenuButton: Boolean,
    onMenuClick: () -> Unit,
) {
    val currentTitle = drawerItems.find { it.route == currentRoute }?.title ?: stringResource(Res.string.nav_home)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentTitle) },
                navigationIcon = {
                    if (showMenuButton) {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Filled.Menu, contentDescription = stringResource(Res.string.menu))
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            CasherAppNavigation(navController = navController)
        }
    }
}
