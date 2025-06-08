package com.example.careconnect.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careconnect.R

// 1. Define soothing color palettes with primary, secondary, tertiary roles
private val LightColors = lightColorScheme(
    primary = Color(0xFF6BAA75),             // soft green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EADC),    // pale mint
    onPrimaryContainer = Color(0xFF1A371F),

    secondary = Color(0xFF8EA3A3),           // muted teal
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDAE4E4),
    onSecondaryContainer = Color(0xFF101F1F),

    tertiary = Color(0xFFFFB74D),            // warm orange accent
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFECB3),
    onTertiaryContainer = Color(0xFF332300),

    background = Color(0xFFF7F8F8),          // off-white
    onBackground = Color(0xFF1A1C1D),
    surface = Color(0xFFF7F8F8),
    onSurface = Color(0xFF1A1C1D),

    error = Color(0xFFBA1A1A),               // gentle red
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9ACFA0),
    onPrimary = Color(0xFF00390B),
    primaryContainer = Color(0xFF20521C),
    onPrimaryContainer = Color(0xFFBCECC2),

    secondary = Color(0xFFB3CAC9),
    onSecondary = Color(0xFF21302F),
    secondaryContainer = Color(0xFF364A49),
    onSecondaryContainer = Color(0xFFE0E7EA),

    tertiary = Color(0xFFFFD180),
    onTertiary = Color(0xFF663F00),
    tertiaryContainer = Color(0xFF4B2E00),
    onTertiaryContainer = Color(0xFFFFE0B2),

    background = Color(0xFF121312),
    onBackground = Color(0xFFE1E3E2),
    surface = Color(0xFF121312),
    onSurface = Color(0xFFE1E3E2),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

// 2. Define a font family (use default or add your .ttf files under res/font)
private val CareFontFamily = FontFamily.Default

// 3. Expanded Typography including Display, Headline, Title, Body, Label
private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp
    ),
    displayMedium = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),

    headlineLarge = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),

    titleLarge = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    labelLarge = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = CareFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)

// 4. Define shapes with gentle rounding
private val AppShapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

/**
 * Application theme for CareConnect (Elderly Care Management).
 * Supports dynamic colors on Android 12+, with primary, secondary, tertiary roles.
 */
@Composable
fun CareConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

/**
 * Button color defaults for primary, secondary, tertiary roles
 */
object CareConnectButtonDefaults {
    @Composable
    fun primaryColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    @Composable
    fun secondaryColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    )

    @Composable
    fun tertiaryColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary
    )
}