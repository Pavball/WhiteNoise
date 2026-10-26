package pavball.hr.whitenoise.ui.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DreamJournalInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    customFont: FontFamily,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,

        // 1. Placeholder Styling
        placeholder = {
            Text(
                text = placeholder,
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
            // Using a slightly transparent white to match the "glassy" look
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(250.dp)
    )
}
