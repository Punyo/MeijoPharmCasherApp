package com.punyo.casherapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import casherapplication.composeapp.generated.resources.Res
import casherapplication.composeapp.generated.resources.*
import com.punyo.casherapp.ui.home.HomeScreen
import com.punyo.casherapp.ui.transactions.TransactionsScreen
import com.punyo.casherapp.ui.settings.SettingsScreen
import com.punyo.casherapp.ui.profile.ProfileScreen
import com.punyo.casherapp.ui.about.AboutScreen

data class DrawerItem(
    val icon: ImageVector,
    val title: String,
    val route: String
)


@Composable
fun DrawerContent(
    drawerItems: List<DrawerItem>,
    selectedItem: String,
    onItemClick: (String) -> Unit,
    onDrawerClose: () -> Unit = {}
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
                modifier = Modifier.padding(horizontal = 12.dp)
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
    content: @Composable (showMenuButton: Boolean, onMenuClick: () -> Unit) -> Unit
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
                        onItemClick = onItemClick
                    )
                }
            }
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
                        }
                    )
                }
            }
        ) {
            content(
                true
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

        val drawerItems = listOf(
            DrawerItem(Icons.Filled.Home, stringResource(Res.string.nav_home), "home"),
            DrawerItem(Icons.Filled.AccountBalance, stringResource(Res.string.nav_transactions), "transactions"),
            DrawerItem(Icons.Filled.Settings, stringResource(Res.string.nav_settings), "settings"),
            DrawerItem(Icons.Filled.Person, stringResource(Res.string.nav_profile), "profile"),
            DrawerItem(Icons.Filled.Info, stringResource(Res.string.nav_about), "about")
        )

        ResponsiveNavigationDrawer(
            drawerItems = drawerItems,
            selectedItem = selectedItem
        ) { showMenuButton, onMenuClick ->
            MainContent(
                selectedItem = selectedItem,
                showMenuButton = showMenuButton,
                onMenuClick = onMenuClick
            )
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = stringResource(Res.string.profile),
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = stringResource(Res.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    selectedItem: String,
    showMenuButton: Boolean,
    onMenuClick: () -> Unit
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
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
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
