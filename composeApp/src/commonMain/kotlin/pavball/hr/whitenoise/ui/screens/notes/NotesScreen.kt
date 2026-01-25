package pavball.hr.whitenoise.ui.screens.notes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.effects.GlowingTimerCircle
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_art
import whitenoise.composeapp.generated.resources.bg_moonlight
import whitenoise.composeapp.generated.resources.sleep

@Composable
fun NotesScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    var isActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_moonlight),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Notes",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA8E6CF) // Mint color from previous screen
            )

        }
    }
}
