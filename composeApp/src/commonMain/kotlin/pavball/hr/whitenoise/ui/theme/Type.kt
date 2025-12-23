package pavball.hr.whitenoise.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DozzzeTypography = Typography(
    // "H1 naslov" - Bold and prominent
    headlineLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp),

    // "H2 naslov" - Slightly lighter, medium size
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        color = DarkNavy
    ),

    // "H3 naslov" - Regular weight, smaller header
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = SlateBlue // Slightly lighter text for sub-headers
    ),

    // "Body text" - Standard reading size
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = SlateBlue
    )
)
