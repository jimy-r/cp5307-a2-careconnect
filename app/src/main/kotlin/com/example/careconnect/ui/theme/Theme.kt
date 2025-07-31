// Defines the package
package com.example.careconnect.ui.theme

// Imports necessary classes and functions from Android and Jetpack Compose libraries.
import android.app.Activity // Needed to get the current window.
import android.os.Build // Used to check the Android version for features like dynamic color.
import androidx.compose.foundation.isSystemInDarkTheme // A utility to check if the device is in dark mode.
import androidx.compose.material3.MaterialTheme // The main component for applying Material Design styling.
import androidx.compose.material3.darkColorScheme // Function to create a color scheme for dark themes.
import androidx.compose.material3.dynamicDarkColorScheme // Creates a dark theme based on the user's wallpaper (Android 12+).
import androidx.compose.material3.dynamicLightColorScheme // Creates a light theme based on the user's wallpaper (Android 12+).
import androidx.compose.material3.lightColorScheme // Function to create a color scheme for light themes.
import androidx.compose.runtime.Composable // Marks a function as a Jetpack Compose UI element.
import androidx.compose.runtime.SideEffect // A composable for executing code that interacts with non-Compose systems.
import androidx.compose.ui.graphics.toArgb // Converts a Compose Color to an Android integer color value.
import androidx.compose.ui.platform.LocalContext // Provides the application's Context.
import androidx.compose.ui.platform.LocalView // Provides the current Android View.
import androidx.core.view.WindowCompat // A compatibility helper for controlling window features like system bars.

// Defines the static color palette for the app's dark theme.
// These `md_theme_dark_*` values are typically defined in another file (e.g. Color.kt).
private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

// Defines the static color palette for the app's light theme.
private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

// This is the main theme composable that wraps the entire app UI.
@Composable
fun CareConnectTheme(
    // Parameter to determine if the dark theme should be used. Defaults to the device's system setting.
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Parameter to enable dynamic colors from the user's wallpaper. Off by default.
    dynamicColour: Boolean = false,
    // The UI content of the app that this theme will be applied to.
    content: @Composable () -> Unit
) {
    // This 'when' block selects the correct color scheme based on the function's parameters.
    val colorScheme = when {
        // If dynamic color is enabled and the OS is Android 12 (SDK 31) or higher...
        dynamicColour && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // ...use the dynamic color scheme (dark or light) from the user's wallpaper.
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // If not using dynamic color, check if dark theme is enabled.
        darkTheme -> DarkColorScheme
        // Otherwise, fall back to the default light theme.
        else -> LightColorScheme
    }

    // This section modifies the system UI (like the status bar) to match the app theme.
    val view = LocalView.current
    // This check ensures the code doesn't run in the Android Studio preview, which can cause crashes.
    if (!view.isInEditMode) {
        // SideEffect is used to safely perform actions on non-Compose objects after a recomposition.
        SideEffect {
            val window = (view.context as Activity).window
            // Set the status bar color to the theme's primary color.
            window.statusBarColor = colorScheme.primary.toArgb()
            // Control the color of status bar icons (time, battery).
            // NOTE: This logic is likely incorrect. It should be `!darkTheme` to ensure icons are light on a dark theme and vice-versa.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    // Applies the chosen colors and typography to the entire UI content passed into the function.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // 'Typography' is defined in Typography.kt
        content = content
    )
}