package pavball.hr.whitenoise.ui.components.utils

import androidx.compose.ui.graphics.Color

/** Colors handed out to newly added custom sounds, in order. */
object CustomSoundPalette {
    private val colors = listOf(
        "FF7986CB", // indigo
        "FF4DB6AC", // teal
        "FFBA68C8", // purple
        "FFFFB74D", // amber
        "FF64B5F6", // blue
        "FFE57373", // red
        "FF98CBB4", // mint
        "FFA1887F"  // brown
    )

    fun colorFor(index: Int): String = colors[index.mod(colors.size)]
}

/**
 * Gradient for a sound card: from the custom sound's stored color when it has
 * one, otherwise the built-in sound's own colors.
 */
fun resolveGradientColors(
    soundKey: String?,
    dbColorId: String?
): List<Color> {
    if (!dbColorId.isNullOrBlank()) {
        val baseColor = Color.fromHex(dbColorId)
        return listOf(baseColor, baseColor.lighten(0.35f))
    }

    return when (soundKey) {
        "ocean" -> listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
        "forest" -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))
        "rain" -> listOf(Color(0xFF546E7A), Color(0xFF90A4AE))
        "thunder" -> listOf(Color(0xFF37474F), Color(0xFF78909C))
        else -> listOf(Color(0xFF3F51B5), Color(0xFF7986CB))
    }
}

fun Color.Companion.fromHex(hex: String?): Color {
    if (hex.isNullOrBlank()) {
        return Color(0xFF9E9E9E) // fallback
    }

    return try {
        val cleanHex = hex
            .removePrefix("#")
            .removePrefix("0x")
            .trim()

        val colorLong = cleanHex.toLong(16)

        Color(
            red = ((colorLong shr 16) and 0xFF) / 255f,
            green = ((colorLong shr 8) and 0xFF) / 255f,
            blue = (colorLong and 0xFF) / 255f,
            alpha = 1f
        )
    } catch (_: Exception) {
        Color(0xFF9E9E9E) // fallback if invalid hex
    }
}


fun Color.lighten(factor: Float = 0.3f): Color {
    return Color(
        red = (this.red + (1f - this.red) * factor),
        green = (this.green + (1f - this.green) * factor),
        blue = (this.blue + (1f - this.blue) * factor)
    )
}
