package pavball.hr.whitenoise.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun DozzzeButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    buttonTextStyle: TextStyle,
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
        Text(text = buttonText, style = buttonTextStyle)
    }


}