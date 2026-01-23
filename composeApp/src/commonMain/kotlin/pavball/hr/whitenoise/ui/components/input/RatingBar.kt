package pavball.hr.whitenoise.ui.components.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    maxRating: Int = 5,
    activeColor: Color = Color.Yellow, // White matches your dark background screenshot
    inactiveColor: Color = Color.White.copy(alpha = 0.5f) // Semi-transparent white
) {
    Row {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Filled.Star,
                contentDescription = "Rate $i stars",
                tint = if (i <= currentRating) activeColor else inactiveColor,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(i) }
                    .padding(4.dp)
            )
        }
    }
}

