package pavball.hr.whitenoise

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import pavball.hr.whitenoise.ui.screens.main.MainScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}