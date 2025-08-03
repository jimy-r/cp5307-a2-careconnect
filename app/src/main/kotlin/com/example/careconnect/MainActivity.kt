// Defines the file's location at the root of the application's package structure.
package com.example.careconnect

// Imports necessary libraries for the main activity, UI components, and navigation.
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

// Marks this Activity as an entry point for Hilt dependency injection.
@AndroidEntryPoint
// Defines the main and only Activity for the application.
class MainActivity : ComponentActivity() {
    // Opts in to using experimental APIs from Material Design 3.
    @OptIn(ExperimentalMaterial3Api::class)
    // This function is called when the Activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calls the parent class's onCreate method to ensure proper setup.
        super.onCreate(savedInstanceState)
        // Sets the content of the Activity to be Jetpack Compose UI.
        setContent {
            // Applies the custom app theme (colors, typography) to all child composables.
            CareConnectTheme {
                // Creates and remembers a NavController to manage app navigation.
                val navController = rememberNavController()
                // Gets the current navigation back stack entry as a state that recomposes on change.
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                // Extracts the string route of the currently displayed screen.
                val currentRoute = currentBackStackEntry?.destination?.route

                // Uses a helper function to get the Screen object for the current route.
                val currentScreen = getScreenFromRoute(currentRoute)
                // Checks if the current screen is part of the main, post-login navigation graph.
                val isMainScreen = currentBackStackEntry?.destination?.parent?.route == MainRoute.ROOT

                // Sets up the main screen structure with top and bottom bars.
                Scaffold(
                    // Defines the top app bar, which is only shown on main screens.
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
                    // Defines the bottom navigation bar, which is only shown on main screens.
                    bottomBar = {
                        if (isMainScreen) {
                            // The container for the bottom navigation items.
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                // Iterates through the list of main screens to create a navigation item for each.
                                listOf(Screen.Home, Screen.Schedule, Screen.Messages, Screen.Journal, Screen.Admin).forEach { screen ->
                                    // A single clickable item in the bottom navigation bar, containing an icon.
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                                        label = null,
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            // Navigates to the corresponding screen when clicked.
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
                    // Hosts the main navigation graph, applying padding to avoid the top/bottom bars.
                    RootNavigationGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}