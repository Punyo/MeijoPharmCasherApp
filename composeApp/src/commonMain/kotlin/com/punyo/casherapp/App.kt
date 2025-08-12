package com.punyo.casherapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.punyo.casherapp.ui.about.AboutScreen
import com.punyo.casherapp.ui.home.HomeScreen
import com.punyo.casherapp.ui.profile.ProfileScreen
import com.punyo.casherapp.ui.settings.SettingsScreen
import com.punyo.casherapp.ui.transactions.TransactionsScreen
import kotlinx.coroutines.launch
import meijopharmcasherapp.composeapp.generated.resources.Res
import meijopharmcasherapp.composeapp.generated.resources.app_name
import meijopharmcasherapp.composeapp.generated.resources.app_subtitle
import meijopharmcasherapp.composeapp.generated.resources.menu
import meijopharmcasherapp.composeapp.generated.resources.nav_about
import meijopharmcasherapp.composeapp.generated.resources.nav_home
import meijopharmcasherapp.composeapp.generated.resources.nav_profile
import meijopharmcasherapp.composeapp.generated.resources.nav_settings
import meijopharmcasherapp.composeapp.generated.resources.nav_transactions
import meijopharmcasherapp.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.stringResource

data class DrawerItem(
    val icon: ImageVector,
    val title: String,
    val route: String,
)

@Composable
fun DrawerContent(
    drawerItems: List<DrawerItem>,
    selectedItem: String,
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
                selected = selectedItem == item.title,
                onClick = {
                    onItemClick(item.title)
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
    selectedItem: String,
    onItemClick: (String) -> Unit = {},
    content: @Composable (showMenuButton: Boolean, onMenuClick: () -> Unit) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val usePermanentDrawer = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    if (usePermanentDrawer) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(modifier = Modifier.width(240.dp)) {
                    DrawerContent(
                        drawerItems = drawerItems,
                        selectedItem = selectedItem,
                        onItemClick = onItemClick,
                    )
                }
            },
        ) {
            content(
                false,
            ) {}
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
                        selectedItem = selectedItem,
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
            content(
                true,
            ) {
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
        val homeTitle = stringResource(Res.string.nav_home)
        var selectedItem by remember { mutableStateOf(homeTitle) }

        val drawerItems =
            listOf(
                DrawerItem(Icons.Filled.Home, stringResource(Res.string.nav_home), "home"),
                DrawerItem(Icons.Filled.AccountBalance, stringResource(Res.string.nav_transactions), "transactions"),
                DrawerItem(Icons.Filled.Settings, stringResource(Res.string.nav_settings), "settings"),
                DrawerItem(Icons.Filled.Person, stringResource(Res.string.nav_profile), "profile"),
                DrawerItem(Icons.Filled.Info, stringResource(Res.string.nav_about), "about"),
            )

        ResponsiveNavigationDrawer(
            drawerItems = drawerItems,
            selectedItem = selectedItem,
        ) { showMenuButton, onMenuClick ->
            MainContent(
                selectedItem = selectedItem,
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
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = stringResource(Res.string.profile),
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
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
    selectedItem: String,
    showMenuButton: Boolean,
    onMenuClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedItem) },
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
            contentAlignment = Alignment.Center,
        ) {
            when (selectedItem) {
                stringResource(Res.string.nav_home) -> HomeScreen()
                stringResource(Res.string.nav_transactions) -> TransactionsScreen()
                stringResource(Res.string.nav_settings) -> SettingsScreen()
                stringResource(Res.string.nav_profile) -> ProfileScreen()
                stringResource(Res.string.nav_about) -> AboutScreen()
                else -> HomeScreen()
            }
        }
    }
}
