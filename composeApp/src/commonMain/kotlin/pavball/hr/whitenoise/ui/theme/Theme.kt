package pavball.hr.whitenoise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// 1. Define Light Scheme
private val LightColorScheme = lightColorScheme(
    primary = SlateBlue,
    onPrimary = White,
    primaryContainer = SlateBlue,
    onPrimaryContainer = DarkNavy,

    secondary = Mint,
    onSecondary = DarkNavy,

    tertiary = ActionBlue,

    background = White,
    onBackground = DarkNavy,

    surface = LightGray,
    onSurface = DarkNavy,
    onPrimaryFixed = LightGray
)

// 2. Define Dark Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Mint,
    onPrimary = DarkNavy,
    primaryContainer = Mint,

    secondary = ActionBlue,
    onSecondary = White,

    tertiary = ActionBlue,
    background = DarkBackground,
    onBackground = White,

    surface = DarkNavy,
    onSurface = White,
    onPrimaryFixed = LightGray
)

@Composable
fun DozzzeTheme(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {

    val colorScheme = if (isDarkMode) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DozzzeTypography,
        content = content
    )
}

