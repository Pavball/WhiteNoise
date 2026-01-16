package pavball.hr.whitenoise.ui.components.button

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun DozzzeButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    onClicked: () -> Unit,
){

    Button(
        onClick = { onClicked() },
        modifier = modifier,
    ){
        Text(text = buttonText)
    }


}