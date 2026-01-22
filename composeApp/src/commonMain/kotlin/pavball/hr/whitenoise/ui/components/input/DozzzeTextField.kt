package pavball.hr.whitenoise.ui.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DreamJournalInput(
    customFont: FontFamily
) {
    // State to hold the user's input
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },

        // 1. Placeholder Styling
        placeholder = {
            Text(
                text = "Dream of the day...",
                style = TextStyle(
                    fontFamily = customFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            )
        },

        // 2. Main Text Styling
        textStyle = TextStyle(
            fontFamily = customFont,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = Color.Black
        ),

        // 3. Container Shape (Rounded Corners)
        shape = RoundedCornerShape(24.dp),

        // 4. Color Overrides to remove Material defaults
        colors = TextFieldDefaults.colors(
            // Background colors (Unfocused & Focused)
            // Using a slightly transparent white to match the "glassy" look if desired
            focusedContainerColor = Color.White.copy(alpha = 0.9f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.9f),

            // Remove the underline indicator
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,

            // Cursor color
            cursorColor = Color.DarkGray
        ),

        // 5. Layout Modifier
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Set a fixed height for the large box
    )
}