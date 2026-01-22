package pavball.hr.whitenoise.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.Font
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.baloo
import whitenoise.composeapp.generated.resources.quicksand_bold
import whitenoise.composeapp.generated.resources.quicksand_medium
import whitenoise.composeapp.generated.resources.quicksand_regular
import whitenoise.composeapp.generated.resources.quicksand_semibold


val DozzzeTypography = Typography(
    // "H1 naslov" - Bold and prominent
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = Mint
    ),

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
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        color = DarkNavy
    ),


    )



@Composable
fun getCustomBalooFontFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.baloo, weight = FontWeight.Normal)
    )
}

@Composable
fun getCustomQuicksandFontFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.quicksand_bold, weight = FontWeight.Bold),
        Font(resource = Res.font.quicksand_semibold, weight = FontWeight.SemiBold),
        Font(resource = Res.font.quicksand_medium, weight = FontWeight.Medium),
        Font(resource = Res.font.quicksand_regular, weight = FontWeight.Normal)
    )
}
