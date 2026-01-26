package pavball.hr.whitenoise.ui.components.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.SlateBlue

@Composable
fun AuthLabel(text: String, font: androidx.compose.ui.text.font.FontFamily) {
    Text(
        text = text,
        fontFamily = font,
        color = DarkNavy,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
