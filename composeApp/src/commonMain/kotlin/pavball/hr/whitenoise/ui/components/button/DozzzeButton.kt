package pavball.hr.whitenoise.ui.components.button

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp


@Composable
fun DozzzeButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    buttonTextStyle: TextStyle,
    buttonTextFont: FontFamily,
    buttonTextWeight: FontWeight,
    buttonWidth: Dp,
    shape: Shape,
    onClicked: () -> Unit,
) {

    Button(
        onClick = { onClicked() },
        modifier = modifier.width(buttonWidth),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = shape
    ) {
        Text(
            text = buttonText,
            style = buttonTextStyle,
            fontFamily = buttonTextFont,
            fontWeight = buttonTextWeight,
            textAlign = TextAlign.Center
        )
    }


}