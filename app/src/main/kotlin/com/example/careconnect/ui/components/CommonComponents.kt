// Defines the file's location within the reusable UI components architecture.
package com.example.careconnect.ui.components

// Imports necessary libraries for layout, Material Design components, and graphics.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Defines a reusable UI component for displaying summary information on the dashboard.
@Composable
fun DashboardCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip
) {
    // Creates a card with a slight shadow to make it stand out from the background.
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Arranges the icon and text horizontally next to each other.
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Displays the vector graphic icon for the card.
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            // Adds a fixed amount of space between the icon and the text.
            Spacer(modifier = Modifier.width(16.dp))
            // Arranges the title and subtitle vertically on top of each other.
            Column(modifier = Modifier.weight(1f)) {
                // Displays the main title text for the card.
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                // Displays the secondary subtitle text, handling multiple lines and overflow.
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = maxLines,
                    overflow = overflow
                )
            }
        }
    }
}

// Defines a reusable, full-width button for navigation on the Admin screen.
@Composable
fun AdminButton(label: String, onClick: () -> Unit) {
    // Creates the clickable button element with specific dimensions and shape.
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        // Displays the text label inside the button.
        Text(label, fontSize = 16.sp)
    }
}

// Defines a simple, reusable circular loading spinner.
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    // Displays the indeterminate progress indicator from Material Design.
    CircularProgressIndicator(modifier = modifier)
}

// Defines a reusable card for displaying error messages with a distinct style.
@Composable
fun ErrorCard(errorMessage: String) {
    // Creates a card with a specific background color to indicate an error.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        // Displays the error message text within the card.
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

