package com.example.careconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.careconnect.ui.theme.CareConnectTheme

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val title: String) {
    object Home     : Screen("home",     Icons.Default.Home,      "CareConnect")
    object Schedule : Screen("schedule", Icons.Default.Schedule,  "Schedule")
    object Messages : Screen("messages", Icons.Default.Message,   "Messaging")
    object Journal  : Screen("journal",  Icons.Default.Book,      "Journal")
    object Admin    : Screen("admin",    Icons.Default.Settings,  "Admin")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareConnectTheme {
                val navController = rememberNavController()
                Scaffold { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MenuRail(navController)
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            NavigationGraph(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuRail(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Schedule,
        Screen.Messages,
        Screen.Journal,
        Screen.Admin
    )
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    NavigationRail(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .fillMaxHeight()
            .width(72.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        items.forEach { screen ->
            NavigationRailItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = false
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route)     { HomeScreen() }
        composable(Screen.Schedule.route) { ScheduleScreen() }
        composable(Screen.Messages.route) { MessagingScreen() }
        composable(Screen.Journal.route)  { CareJournalScreen() }
        composable(Screen.Admin.route)    { AdminSectionScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Screen.Home.title, style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardItem("Medication Reminder", "Aspirin 8:00 AM")
            CardItem("Health Overview", "No new alerts")
            CardItem("Upcoming Appointment", "Doctor • Apr 26 10:00 AM")
            CardItem("Step Count", "1,200 steps today")
        }
    }
}

@Composable
fun CardItem(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Screen.Schedule.title, style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { inner ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(inner)
            .padding(16.dp)
        ) {
            val month = "April 2024"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, null) }
                Text(month, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowForward, null) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val days = listOf("S","M","T","W","T","F","S")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                days.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                (17..23).forEach { day ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (day == 23) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day.toString(), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ListItem(Icons.Default.EventNote, "9:00 AM", "Doctor's appointment")
            ListItem(Icons.Default.Medication, "10:00 AM", "Aspirin")
        }
    }
}

@Composable
fun ListItem(icon: androidx.compose.ui.graphics.vector.ImageVector, time: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(time, style = MaterialTheme.typography.labelMedium)
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Screen.Messages.title, style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChatBubble("Hi, how's grandma today?", false)
                ChatBubble("She took her medication on time.", true)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") }
                )
                IconButton(onClick = {}) { Icon(Icons.Default.Send, null) }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(if (isUser) Alignment.End else Alignment.Start)
            .background(
                if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareJournalScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Screen.Journal.title, style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            JournalEntry("Apr 25, 2024", "Assisted with mobility exercises today. Noticed improvement in gait.")
            JournalEntry("Apr 24, 2024", "Changed dosage; monitoring side effects.")
            JournalEntry("Apr 21, 2024", "Daughter visited; resident engaged well.")
        }
    }
}

@Composable
fun JournalEntry(date: String, note: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(note, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSectionScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Screen.Admin.title, style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileItem("Ellen Roberts")
            AdminButton("Personal Info")
            AdminButton("Caregivers")
            AdminButton("Accessibility")
            AdminButton("Settings")
        }
    }
}

@Composable
fun ProfileItem(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun AdminButton(label: String) {
    Button(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(label)
    }
}