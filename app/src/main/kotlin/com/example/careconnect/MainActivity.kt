// Defines the package this file belongs to, which is the root package for the app.
package com.example.careconnect

// Imports all the necessary classes and functions required for this file.
import android.os.Bundle // For saving instance state in the activity lifecycle.
import androidx.activity.ComponentActivity // The base class for activities that use Jetpack Compose.
import androidx.activity.compose.setContent // A helper to set the Compose UI content for the activity.
// --- Jetpack Compose Layout Imports ---
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme // Detects if the device is in dark mode.
import androidx.compose.foundation.layout.* // Imports all standard layout components like Column, Row, Spacer, etc.
import androidx.compose.foundation.lazy.LazyColumn // For creating efficient, scrollable lists.
import androidx.compose.foundation.lazy.items // An item provider for LazyColumn.
import androidx.compose.foundation.shape.CircleShape // Pre-defined shapes.
import androidx.compose.foundation.shape.RoundedCornerShape
// --- Material Design 3 Icon Imports ---
import androidx.compose.material.icons.Icons // The library for Material icons.
import androidx.compose.material.icons.automirrored.filled.* // Auto-mirrored icons adjust for right-to-left languages.
import androidx.compose.material.icons.filled.* // Standard Material icons.
// --- Material Design 3 Component Imports ---
import androidx.compose.material3.* // Imports main M3 components like Button, Card, Scaffold, etc.
// --- Jetpack Compose Runtime Imports ---
import androidx.compose.runtime.Composable // Marks a function as a UI-building block.
import androidx.compose.runtime.getValue // A delegate to easily access the value of a State object.
// --- Jetpack Compose UI Imports ---
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // The primary tool for decorating or adding behavior to composables.
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Represents a vector graphic, like an icon.
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // Annotation for rendering composable previews in Android Studio.
import androidx.compose.ui.unit.dp // For specifying dimensions in density-independent pixels.
import androidx.compose.ui.unit.sp // For specifying font sizes in scale-independent pixels.
// --- Jetpack Navigation Imports ---
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost // The container for navigating between composables.
import androidx.navigation.compose.composable // Defines a specific navigation destination.
import androidx.navigation.compose.currentBackStackEntryAsState // Observes the navigation back stack.
import androidx.navigation.compose.rememberNavController // Creates and remembers a NavHostController.
// --- Local Project Imports ---
import com.example.careconnect.ui.theme.CareConnectTheme // Imports the custom theme for the app.

// A sealed interface defines a restricted class hierarchy for all the main screens.
// This ensures type safety and that all screen types are known at compile time.
sealed interface Screen {
    val route: String // The unique string path for navigation.
    val icon: ImageVector // The icon for the bottom navigation bar.
    val title: String // The title for the top app bar.

    // Each 'data object' represents a specific screen, providing its unique properties.
    data object Home : Screen {
        override val route: String = "home"
        override val icon: ImageVector = Icons.Default.Home
        override val title: String = "CareConnect"
    }
    data object Schedule : Screen {
        override val route: String = "schedule"
        override val icon: ImageVector = Icons.Default.Schedule
        override val title: String = "Schedule"
    }
    data object Messages : Screen {
        override val route: String = "messages"
        override val icon: ImageVector = Icons.AutoMirrored.Filled.Message
        override val title: String = "Messaging"
    }
    data object Journal : Screen {
        override val route: String = "journal"
        override val icon: ImageVector = Icons.Default.Book
        override val title: String = "Journal"
    }
    data object Admin : Screen {
        override val route: String = "admin"
        override val icon: ImageVector = Icons.Default.Settings
        override val title: String = "Admin"
    }
}

// This OptIn is required to use new, experimental Material 3 APIs like TopAppBar.
@OptIn(ExperimentalMaterial3Api::class)
// This is the main entry point of the app, inheriting from ComponentActivity.
class MainActivity : ComponentActivity() {
    // This function is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent is the entry point for Jetpack Compose UI.
        setContent {
            // Applies the custom theme defined in ui.theme/Theme.kt to the entire app.
            CareConnectTheme(darkTheme = isSystemInDarkTheme()) {
                // Creates and remembers a navigation controller to manage app navigation.
                val navController = rememberNavController()
                // Observes the navigation back stack to get the current screen's entry.
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                // Extracts the route string from the current back stack entry.
                val currentRoute = currentBackStackEntry?.destination?.route

                // Finds the `Screen` object that matches the current route to get its title.
                val currentScreen = listOf(
                    Screen.Home,
                    Screen.Schedule,
                    Screen.Messages,
                    Screen.Journal,
                    Screen.Admin
                ).find { it.route == currentRoute }

                // Scaffold provides a standard layout structure for Material Design apps.
                Scaffold(
                    // Defines the top app bar. It's only shown if a `currentScreen` is found.
                    topBar = {
                        if (currentScreen != null) {
                            TopAppBar(
                                title = { Text(currentScreen.title) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    },
                    // Defines the bottom navigation bar.
                    bottomBar = {
                        AppBottomNavigation(navController = navController)
                    }
                ) { innerPadding ->
                    // The main content area of the app. The `innerPadding` prevents content from
                    // being hidden behind the top and bottom bars.
                    NavigationGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// This composable function builds the bottom navigation bar.
@Composable
fun AppBottomNavigation(navController: NavHostController) {
    // Defines the list of screens to be displayed in the navigation bar.
    val items = listOf(
        Screen.Home,
        Screen.Schedule,
        Screen.Messages,
        Screen.Journal,
        Screen.Admin
    )
    // Gets the current route to determine which navigation item is selected.
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    // The container for the bottom navigation bar items.
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        // Loops through the list of screens to create an item for each one.
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = null, // No text label is shown for the items.
                selected = currentRoute == screen.route, // The item is selected if its route matches the current route.
                // Defines the action to perform when the item is clicked.
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination to avoid building a large back stack.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same screen on top of each other.
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item.
                        restoreState = true
                    }
                },
                // Defines the colors for the selected and unselected states.
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// This composable defines the app's navigation graph.
@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    // NavHost is the container that displays the correct screen composable for the current route.
    NavHost(
        navController,
        startDestination = Screen.Home.route, // The screen to show when the app first starts.
        modifier = modifier
    ) {
        // Each 'composable' block links a route string to a screen composable.
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Schedule.route) { ScheduleScreen() }
        composable(Screen.Messages.route) { MessagingScreen() }
        composable(Screen.Journal.route) { CareJournalScreen() }
        composable(Screen.Admin.route) { AdminSectionScreen() }
    }
}

// Displays the main dashboard screen.
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineSmall)
        // Uses a reusable CardItem composable to display information.
        CardItem(
            icon = Icons.Default.Medication,
            title = "Medication Reminder",
            subtitle = "Aspirin 8:00 AM"
        )
        CardItem(
            icon = Icons.Default.FavoriteBorder,
            title = "Health Overview",
            subtitle = "No new alerts"
        )
        CardItem(
            icon = Icons.Default.Event,
            title = "Upcoming Appointment",
            subtitle = "Doctor • 31 Jul 10:00 AM"
        )
        CardItem(
            icon = Icons.Default.DirectionsWalk,
            title = "Step Count",
            subtitle = "1,200 steps today"
        )
    }
}

// A reusable composable for displaying an elevated card with an icon and text.
@Composable
fun CardItem(icon: ImageVector, title: String, subtitle: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min), // Ensures the row height fits its content.
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Decorative icon.
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(verticalArrangement = Arrangement.Center) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Displays the schedule/calendar screen.
@Composable
fun ScheduleScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Row for the month and navigation arrows.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("July 2025", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Row for the day labels (S, M, T, W, T, F, S).
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(it, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Row for the calendar days. A simple, static example.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            (27..31).toList().plus(1..2).forEach { day ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        // Highlights the 31st day with a colored background.
                        .background(
                            if (day == 31) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        day.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (day == 31) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        // Displays a list of appointments for the selected day.
        ListItem(Icons.AutoMirrored.Filled.EventNote, "9:00 AM", "Doctor's appointment")
        ListItem(Icons.Default.Medication, "10:00 AM", "Aspirin")
    }
}

// A reusable composable for displaying a list item with an icon, text, and time.
@Composable
fun ListItem(icon: ImageVector, time: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text, style = MaterialTheme.typography.bodyLarge)
            Text(time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Displays the messaging/chat screen.
@Composable
fun MessagingScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // LazyColumn efficiently displays a potentially long list of messages.
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Takes up all available space, pushing the input bar to the bottom.
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Static example chat bubbles.
            item { ChatBubble("Hi, how's grandma today?", false) }
            item { ChatBubble("She took her medication on time.", true) }
        }
        // Surface provides elevation for the input bar.
        Surface(shadowElevation = 8.dp) {
            // Row for the text input field and send button.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }
        }
    }
}

// A composable for a single chat bubble.
@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    // Determines alignment, colors, and shape based on whether the message is from the current user.
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val colors = if (isUser) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    }
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        // The card that forms the bubble.
        Card(
            modifier = Modifier.widthIn(max = 300.dp), // Constrains the max width of the bubble.
            // Creates the distinctive "tailed" shape for the chat bubble.
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp
            ),
            colors = colors
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp),
                color = textColor
            )
        }
    }
}

// Displays the care journal screen with a list of entries.
@Composable
fun CareJournalScreen() {
    // Static list of journal entries for the example.
    val journalEntries = listOf(
        "30 Jul, 2025" to "Assisted with mobility exercises today. Noticed improvement in gait.",
        "29 Jul, 2025" to "Changed dosage; monitoring side effects.",
        "28 Jul, 2025" to "Daughter visited; resident engaged well."
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Care Journal", style = MaterialTheme.typography.headlineSmall)
        }
        // `items` is a helper for displaying lists in LazyColumn.
        items(journalEntries) { (date, note) ->
            JournalEntry(date, note)
        }
    }
}

// A reusable composable for a single journal entry card.
@Composable
fun JournalEntry(date: String, note: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                date,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(note, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// Displays the admin/settings screen.
@Composable
fun AdminSectionScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileItem("Ellen Roberts")
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        AdminButton("Personal Info")
        AdminButton("Caregivers")
        AdminButton("Accessibility")
        AdminButton("Settings")
    }
}

// Displays the user's profile picture and name.
@Composable
fun ProfileItem(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}

// A reusable button for the admin screen.
@Composable
fun AdminButton(label: String) {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(label, fontSize = 16.sp)
    }
}

// --- PREVIEWS ---
// Previews are used by Android Studio to render composables without running the app.

// Previews the entire app shell in light mode.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Full App Light Mode")
@Composable
fun AppPreviewLight() {
    CareConnectTheme(darkTheme = false) {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { AppBottomNavigation(navController) },
            topBar = { TopAppBar(title = { Text("CareConnect") }) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                HomeScreen()
            }
        }
    }
}

// Previews just the Schedule screen.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Schedule Screen Light")
@Composable
fun ScheduleScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        ScheduleScreen()
    }
}

// Previews just the Messaging screen.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Messaging Screen Light")
@Composable
fun MessagingScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        MessagingScreen()
    }
}