package com.example.careconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.ui.navigation.MainRoute
import com.example.careconnect.ui.navigation.RootNavigationGraph
import com.example.careconnect.ui.navigation.Screen
import com.example.careconnect.ui.navigation.getScreenFromRoute
import com.example.careconnect.ui.theme.CareConnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareConnectTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                val currentScreen = getScreenFromRoute(currentRoute)
                val isMainScreen = currentBackStackEntry?.destination?.parent?.route == MainRoute.ROOT

                Scaffold(
                    topBar = {
                        if (isMainScreen && currentScreen?.title != null) {
                            TopAppBar(
                                title = { Text(currentScreen.title) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (isMainScreen) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                listOf(Screen.Home, Screen.Schedule, Screen.Messages, Screen.Journal, Screen.Admin).forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                                        // This label is correctly set to null to hide the text
                                        label = null,
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    // Passes the padding to the NavHost
                    RootNavigationGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}