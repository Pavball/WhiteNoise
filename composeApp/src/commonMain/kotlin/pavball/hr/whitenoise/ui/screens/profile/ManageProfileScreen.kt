package pavball.hr.whitenoise.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_cloudynight
import whitenoise.composeapp.generated.resources.bg_mountains

@Composable
fun ManageProfileScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    var isActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_cloudynight),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Manage profile",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

        }
    }
}