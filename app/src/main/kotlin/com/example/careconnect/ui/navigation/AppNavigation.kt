package com.example.careconnect.ui.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.example.careconnect.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    data object Home : Screen("home", "Dashboard", Icons.Default.Home)
    data object Schedule : Screen("schedule", "Schedule", Icons.Default.Schedule)
    data object Messages : Screen("messages", "Messages", Icons.AutoMirrored.Filled.Message)
    data object Journal : Screen("journal", "Journal", Icons.Default.Book)
    data object Admin : Screen("admin", "Admin", Icons.Default.Settings)
    data object Contacts : Screen("contacts", "Contact List", null)
    data object Profile : Screen("profile", "Edit Profile", null)
}

object AuthRoute {
    const val ROOT = "auth_root"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

object MainRoute {
    const val ROOT = "main_root"
}

@Composable
fun RootNavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = hiltViewModel()

    if (BuildConfig.DEBUG) {
        try {
            // Use the special IP '10.0.2.2' to connect from the Android Emulator to the host machine
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Firebase.auth.useEmulator("10.0.2.2", 9099)
            Log.d("Emulators", "Using Firebase Emulators for Auth and Firestore")
        } catch (e: IllegalStateException) {
            Log.w("Emulators", "Emulators already running or failed to initialize.")
        }
    }

    NavHost(
        navController = navController,
        startDestination = AuthRoute.ROOT,
        modifier = modifier
    ) {
        navigation(route = AuthRoute.ROOT, startDestination = AuthRoute.LOGIN) {
            composable(AuthRoute.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(MainRoute.ROOT) {
                            popUpTo(AuthRoute.ROOT) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(AuthRoute.REGISTER)
                    }
                )
            }
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

        navigation(route = MainRoute.ROOT, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Schedule.route) { ScheduleScreen() }
            composable(Screen.Messages.route) { MessagingScreen() }
            composable(Screen.Journal.route) { JournalScreen() }
            composable(Screen.Admin.route) { AdminScreen(navController = navController) }
            composable(Screen.Contacts.route) { CaregiversScreen(navController = navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
        }
    }
}

// Helper to find the current Screen object based on the route
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

