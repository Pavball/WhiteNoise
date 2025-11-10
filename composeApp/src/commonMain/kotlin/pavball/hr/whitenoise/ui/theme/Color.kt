package pavball.hr.whitenoise.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val BluePrimary = Color(0xFF2196F3)
val BlueSecondary = Color(0xFF64B5F6)
val DarkBackground = Color(0xFF121212)
val LightBackground = Color(0xFFFFFFFF)

val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    background = LightBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

val DarkColorScheme = darkColorScheme(
    primary = BlueSecondary,
    secondary = BluePrimary,
    background = DarkBackground,
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)
