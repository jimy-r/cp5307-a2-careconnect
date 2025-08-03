// Defines the file's location within the UI navigation architecture.
package com.example.careconnect.ui.navigation

// Imports necessary libraries for logging, UI components, navigation, and Firebase.
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.careconnect.BuildConfig
import com.example.careconnect.ui.screen.HomeScreen
import com.example.careconnect.ui.screen.JournalScreen
import com.example.careconnect.ui.screen.MessagingScreen
import com.example.careconnect.ui.screen.ScheduleScreen
import com.example.careconnect.ui.screen.admin.AdminScreen
import com.example.careconnect.ui.screen.admin.CaregiversScreen
import com.example.careconnect.ui.screen.admin.ProfileScreen
import com.example.careconnect.ui.screen.auth.LoginScreen
import com.example.careconnect.ui.screen.auth.RegistrationScreen
import com.example.careconnect.ui.screen.schedule.AddEditEventScreen
import com.example.careconnect.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Defines all possible navigation destinations in the app as type-safe objects.
sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    data object Home : Screen("home", "Dashboard", Icons.Default.Home)
    data object Schedule : Screen("schedule", "Schedule", Icons.Default.Schedule)
    data object Messages : Screen("messages", "Messages", Icons.AutoMirrored.Filled.Message)
    data object Journal : Screen("journal", "Journal", Icons.Default.Book)
    data object Admin : Screen("admin", "Admin", Icons.Default.Settings)
    data object Contacts : Screen("contacts", "Contact List", null)
    data object Profile : Screen("profile", "Edit Profile", null)
    data object AddEditEvent : Screen("add_edit_event/{date}", "Add/Edit Entry", null) {
        fun createRoute(dateEpochDay: Long) = "add_edit_event/$dateEpochDay"
    }
}

// Defines constant routes for the nested authentication navigation graph.
object AuthRoute {
    const val ROOT = "auth_root"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

// Defines the constant route for the main, post-login part of the app.
object MainRoute {
    const val ROOT = "main_root"
}

// Defines the constant route for the initial splash screen.
object RootRoute {
    const val SPLASH = "splash"
}

// Defines the master navigation graph for the entire application.
@Composable
fun RootNavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    // Retrieves an instance of the AuthViewModel for the authentication flow.
    val authViewModel: AuthViewModel = hiltViewModel()

    // Configures the app to use local Firebase emulators during debug builds.
    if (BuildConfig.DEBUG) {
        try {
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Firebase.auth.useEmulator("10.0.2.2", 9099)
            Log.d("Emulators", "Using Firebase Emulators for Auth and Firestore")
        } catch (e: IllegalStateException) {
            Log.w("Emulators", "Emulators already running or failed to initialize.")
        }
    }

    // Sets up the main navigation container with a splash screen as the starting point.
    NavHost(
        navController = navController,
        startDestination = RootRoute.SPLASH,
        modifier = modifier
    ) {
        // Defines the splash screen destination.
        composable(RootRoute.SPLASH) {
            SplashScreen(navController = navController)
        }

        // Defines the nested graph for all authentication-related screens.
        navigation(route = AuthRoute.ROOT, startDestination = AuthRoute.LOGIN) {
            // Defines the login screen destination within the authentication graph.
            composable(AuthRoute.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(MainRoute.ROOT) {
                            popUpTo(AuthRoute.ROOT) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(AuthRoute.REGISTER) }
                )
            }
            // Defines the registration screen destination within the authentication graph.
            composable(AuthRoute.REGISTER) {
                RegistrationScreen(
                    viewModel = authViewModel,
                    onRegistrationSuccess = {
                        navController.navigate(MainRoute.ROOT) {
                            popUpTo(AuthRoute.ROOT) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Defines the nested graph for all main application screens after login.
        navigation(route = MainRoute.ROOT, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Schedule.route) { ScheduleScreen(navController = navController) }
            composable(Screen.Messages.route) { MessagingScreen() }
            composable(Screen.Journal.route) { JournalScreen() }
            composable(Screen.Admin.route) { AdminScreen(navController = navController) }
            composable(Screen.Contacts.route) { CaregiversScreen(navController = navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
            // Defines the route for adding an event, specifying it expects a 'date' argument.
            composable(
                route = Screen.AddEditEvent.route,
                arguments = listOf(navArgument("date") { type = NavType.LongType })
            ) {
                AddEditEventScreen(navController = navController)
            }
        }
    }
}

// Defines a composable that decides whether to show the login or main app screen.
@Composable
fun SplashScreen(navController: NavHostController) {
    // A side-effect that runs once to check the user's login status.
    LaunchedEffect(key1 = true) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // Navigates to the main app if the user is already logged in.
            navController.navigate(MainRoute.ROOT) {
                popUpTo(RootRoute.SPLASH) { inclusive = true }
            }
        } else {
            // Navigates to the login flow if the user is not logged in.
            navController.navigate(AuthRoute.ROOT) {
                popUpTo(RootRoute.SPLASH) { inclusive = true }
            }
        }
    }

    // Displays a loading spinner while the login check is performed.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


// A helper function to get the current Screen object for updating the top app bar title.
fun getScreenFromRoute(route: String?): Screen? {
    return when (route) {
        Screen.Home.route -> Screen.Home
        Screen.Schedule.route -> Screen.Schedule
        Screen.Messages.route -> Screen.Messages
        Screen.Journal.route -> Screen.Journal
        Screen.Admin.route -> Screen.Admin
        Screen.Contacts.route -> Screen.Contacts
        Screen.Profile.route -> Screen.Profile
        else -> null
    }
}

